/*
 * Add copyright here.
 */

package labs.client;

import labs.pm.data.Drink;
import labs.pm.data.Food;
import labs.pm.data.Product;
import labs.pm.data.Review;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author vishalkushwaha
 **/
public class ResourceFormatter {
    private Locale locale;
    private ResourceBundle resources;
    private DateTimeFormatter dateFormat;
    private NumberFormat moneyFormat;
    private static final Map<String, ResourceFormatter> formatters = Map.of(
            "en-GB", new ResourceFormatter(Locale.UK),
            "en-US", new ResourceFormatter(Locale.US),
            "ru-RU", new ResourceFormatter(Locale.of("ru", "RU")),
            "fr-FR", new ResourceFormatter(Locale.FRANCE),
            "zh-CN", new ResourceFormatter(Locale.CHINA));

    public ResourceFormatter(Locale locale) {
        this.locale = locale;
        this.resources = ResourceBundle.getBundle("labs.client.resources", locale);
        this.dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        this.moneyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    public NumberFormat getMoneyFormat() {
        return moneyFormat;
    }

    public String formatProduct(Product product) {
        String type = switch (product) {
            case Food food -> resources.getString("food");
            case Drink drink -> resources.getString("drink");
        };

        return MessageFormat.format(resources.getString("product"), product.getName(),
                moneyFormat.format(product.getPrice()), product.getRating().getStars(),
                dateFormat.format(product.getBestBefore()), type);
    }

    public String formatReview(Review review) {
        return MessageFormat.format(resources.getString("review"), review.rating().getStars(), review.comments());
    }

    public String getKey(String key) {
        return resources.getString(key);
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public ResourceFormatter changeLocale(String languageTag) {
        return formatters.getOrDefault(languageTag, formatters.get("en-GB"));
    }
}
