/*
 * Add copyright here.
 */

package labs.pm.data;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

/**
 * @author vishalkushwaha
 **/
public class ProductManager {

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());
    private static final ProductManager pm = new ProductManager();
    private static Map<String, ResourceFormatter> formatters = Map.of(
            "en-GB", new ResourceFormatter(Locale.UK),
            "en-US", new ResourceFormatter(Locale.US),
            "ru-RU", new ResourceFormatter(Locale.of("ru", "RU")),
            "fr-FR", new ResourceFormatter(Locale.FRANCE),
            "zh-CN", new ResourceFormatter(Locale.CHINA));
    private Map<Product, List<Review>> products = new HashMap<>();
    private ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");
    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private Path reportsFolder;
    private Path dataFolder;
    private Path tempFolder;

    private ProductManager() {
        try {
            reportsFolder = Path.of(config.getString("reports.folder"));
            dataFolder = Path.of(config.getString("data.folder"));
            tempFolder = Path.of(config.getString("temp.folder"));
            if (Files.notExists(reportsFolder))
                Files.createDirectories(reportsFolder);
            if (Files.notExists(dataFolder))
                Files.createDirectories(dataFolder);
            if (Files.notExists(tempFolder))
                Files.createDirectories(tempFolder);
            reportsFolder = reportsFolder.toRealPath();
            dataFolder = dataFolder.toRealPath();
            tempFolder = tempFolder.toRealPath();
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Unable to create directories to store relevant files " + e.getMessage(), e);
        }
        loadAllData();

    }

    public static ProductManager getInstance(){
        return pm;
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public ResourceFormatter changeLocale(String languageTag) {
        return formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product findProduct(int id) throws ProductManagerException {
        return products
                .keySet()
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProductManagerException("Product With id: " + id + " not found"));
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        try {
            return reviewProduct(findProduct(id), rating, comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
            return null;
        }
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        products.remove(product);
        reviews.add(new Review(rating, comments));
        int sum = 0;
        for (Review review : reviews) {
            sum += review.rating().ordinal();
        }

        product = product.applyRating(Rateable.convert(Math.round((float) sum / reviews.size())));
        products.put(product, reviews);
        return product;
    }

    public void printProductReport(int id, String languageTag) {
        try {
            printProductReport(findProduct(id), languageTag);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }

    public void printProductReport(Product product, String languageTag) {
        ResourceFormatter formatter = changeLocale(languageTag);
        Path productFile = reportsFolder
                .resolve(MessageFormat.format(config.getString("report.file"), product.getId()));

        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.TRUNCATE_EXISTING),
                        StandardCharsets.UTF_8))) {
            out.append(formatter.formatProduct(product) + System.lineSeparator());

            List<Review> reviews = products.get(product);
            Collections.sort(reviews);
            if (reviews.isEmpty()) {
                out.append(formatter.getKey("no.reviews") + System.lineSeparator());
            } else {
                out.append(reviews
                        .stream()
                        .map(review -> formatter.formatReview(review) + System.lineSeparator())
                        .collect(Collectors.joining()));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error printing product report " + e.getMessage(), e);
        }

    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
        ResourceFormatter formatter = changeLocale(languageTag);
        StringBuilder txt = new StringBuilder();
        txt.append(products
                .keySet()
                .stream()
                .filter(filter)
                .sorted(sorter)
                .map(product -> formatter.formatProduct(product) + "\n")
                .collect(Collectors.joining()));

        System.out.println(txt);
    }

    private void dumpData() {
        try {
            Path tempFile = tempFolder.resolve(MessageFormat.format(
                    config.getString("temp.file"), Instant.now()));
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
                out.writeObject(products);
                products = new HashMap<>();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error dumping data " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreDate() {
        try {
            Path tempFile = Files.list(tempFolder).filter(p -> p.toString().endsWith(".tmp")).findFirst().orElseThrow();
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
                products = (HashMap) in.readObject();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error restoring data " + e.getMessage());
        }
    }

    private void loadAllData() {
        try {
            products = Files
                    .list(dataFolder)
                    .filter(p -> p.toString().contains("product"))
                    .map(p -> this.loadProduct(p))
                    .filter(p -> p != null)
                    .collect(Collectors.toMap(p -> p, p -> loadReviews(p)));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading data " + e.getMessage(), e);
        }
    }

    private Product loadProduct(Path file) {
        Product product = null;
        try {
            product = parseProduct(Files
                    .lines(dataFolder.resolve(file), Charset.forName("utf-8"))
                    .findFirst().orElseThrow());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading the product " + e.getMessage());
        }
        return product;
    }

    private List<Review> loadReviews(Product product) {
        List<Review> reviews = null;
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)) {
            reviews = new ArrayList<>();
        } else {
            try {
                reviews = Files
                        .lines(file, Charset.forName("utf-8"))
                        .map(s -> this.parseReview(s))
                        .filter(r -> r != null)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error loading reviews " + e.getMessage());
            }
        }
        return reviews;
    }

    private Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])),
                    (String) values[1]);

        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing review " + text, e);
        }
        return review;
    }

    private Product parseProduct(String text) {
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
            switch ((String) values[0]) {
                case "D":
                    product = new Drink(id, name, price, rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
                    product = new Food(id, name, price, rating, bestBefore);
                default:
                    break;
            }

        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            logger.log(Level.WARNING, "Error parsing product " + text + " " + e.getMessage(), e);
        }
        return product;
    }

    public Map<String, String> getDiscounts(String languageTag) {
        ResourceFormatter formatter = changeLocale(languageTag);
        return products
                .keySet()
                .stream()
                .collect(
                        Collectors.groupingBy(p -> p.getRating().getStars(),
                                Collectors.collectingAndThen(
                                        Collectors.summingDouble(p -> p.getDiscount().doubleValue()),
                                        discount -> formatter.moneyFormat.format(discount))));
    }

    private static class ResourceFormatter {
        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            this.resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
            this.dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            this.moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            String type = switch (product) {
                case Food food -> resources.getString("food");
                case Drink drink -> resources.getString("drink");
            };

            return MessageFormat.format(resources.getString("product"), product.getName(),
                    moneyFormat.format(product.getPrice()), product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()), type);
        }

        private String formatReview(Review review) {
            return MessageFormat.format(resources.getString("review"), review.rating().getStars(), review.comments());
        }

        private String getKey(String key) {
            return resources.getString(key);
        }

    }

}
