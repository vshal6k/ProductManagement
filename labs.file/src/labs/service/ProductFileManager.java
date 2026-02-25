/*
 * Add copyright here.
 */

package labs.service;

import labs.pm.service.ProductManager;
import labs.pm.service.ProductManagerException;
import labs.pm.data.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.concurrent.locks.Lock;

/**
 * @author vishalkushwaha
 **/
public class ProductFileManager implements ProductManager {

    private static final Logger logger = Logger.getLogger(ProductFileManager.class.getName());
    private static final ProductFileManager pm = new ProductFileManager();
    private Map<Product, List<Review>> products = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
    private final ResourceBundle config = ResourceBundle.getBundle("labs.service.config");
    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private final Path reportsFolder = Path.of(config.getString("reports.folder")).toAbsolutePath();
    private final Path dataFolder = Path.of(config.getString("data.folder")).toAbsolutePath();
    private final Path tempFolder = Path.of(config.getString("temp.folder")).toAbsolutePath();

    private ProductFileManager() {
        try {
            if (Files.notExists(reportsFolder))
                Files.createDirectories(reportsFolder);
            if (Files.notExists(dataFolder))
                Files.createDirectories(dataFolder);
            if (Files.notExists(tempFolder))
                Files.createDirectories(tempFolder);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Unable to create directories to store relevant files " + e.getMessage(), e);
        }
        loadAllData();
    }

    public static ProductFileManager provider() {
        return pm;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = null;
        try {
            writeLock.lock();
            product = new Food(id, name, price, rating, bestBefore);
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception e) {
            logger.log(Level.INFO, "Error adding product " + e.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = null;
        try {
            writeLock.lock();
            product = new Drink(id, name, price, rating);
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception e) {
            logger.log(Level.INFO, "Error adding product " + e.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }
        return product;
    }

    public Product findProduct(int id) throws ProductManagerException {
        try {
            readLock.lock();
            return products
                    .keySet()
                    .stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ProductManagerException("Product With id: " + id + " not found"));
        } finally {
            readLock.unlock();
        }
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        try {
            writeLock.lock();
            return reviewProduct(findProduct(id), rating, comments);
        } catch (Exception e) {
            logger.log(Level.INFO, "Unable to review the product " + e.getMessage());
            return null;
        }finally{
            writeLock.unlock();
        }
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = null;
        try {
            writeLock.lock();
            reviews = products.get(product);
            products.remove(product);
            reviews.add(new Review(rating, comments));
            int sum = 0;
            for (Review review : reviews) {
                sum += review.rating().ordinal();
            }
            product = product.applyRating(Rateable.convert(Math.round((float) sum / reviews.size())));
            products.put(product, reviews);   
        } catch (Exception e) {
            logger.log(Level.INFO, "Unable to review the product " + e.getMessage());
        }finally{
            writeLock.unlock();
        }
        return product;
    }

    private void dumpData() {
        try {
            Path tempFile = tempFolder.resolve(MessageFormat.format(
                    config.getString("temp.file"), Instant.now()));
            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
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
            try (ObjectInputStream in = new ObjectInputStream(
                    Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
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

    public Map<String, BigDecimal> getDiscounts() {
        try{
            readLock.lock();
            return products
                    .keySet()
                    .stream()
                    .collect(
                            Collectors.groupingBy(p -> p.getRating().getStars(),
                                    Collectors.collectingAndThen(
                                            Collectors.summingDouble(p -> p.getDiscount().doubleValue()),
                                            discount -> BigDecimal.valueOf(discount).setScale(2, RoundingMode.HALF_UP))
                            ));
        }finally{
            readLock.unlock();
        }
    }
}
