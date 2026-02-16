/*
 * Add copyright here.
 */

package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

/**
 * @author vishalkushwaha
 * @version 1.0
 *          {@code Shop} class represents an application that manages products.
 **/
public class Shop {

        public static void main(String[] args) {

                ProductManager pm = new ProductManager("en-GB");

                // Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
                pm.parseProduct("F, 101, Tea, 1.69, 2, 2021-11-11");

                try {
                        pm.parseReview("101, 4, Nice hot cup of tea!");
                        pm.parseReview("101, 4, Nice hot cup of tea!");
                        pm.parseReview("101, 2, Rather weak tea");
                        pm.parseReview("101, 4, Fine tea");
                        pm.parseReview("101, 4, Good tea");
                        pm.parseReview("101, 5, Perfect tea");
                        pm.parseReview("101, 3, Just add some lemon");

                } catch (ProductManagerException e) {

                }

                pm.printProductReport(101);

                pm.changeLocale("en-GB");

                Product p2 = pm.createProduct(102, "Coffee", BigDecimal.valueOf(4.99),
                                Rating.FOUR_STAR);
                try {
                        pm.parseReview("102, 2, Good tea");
                        pm.parseReview("102, 3, Good tea");
                        pm.parseReview("102, 1, Nice hot cup of tea!");
                        pm.parseReview("102, 2, Good tea");
                        pm.parseReview("102, 3, Good tea");
                } catch (ProductManagerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                pm.printProductReport(102);

                Comparator<Product> ratingSorter = (p3, p4) -> p4.getRating().ordinal() -
                                p3.getRating().ordinal();
                Comparator<Product> priceSorter = (p3, p4) -> p3.getPrice().compareTo(p4.getPrice());

                Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99),
                                Rating.FIVE_STAR, LocalDate.now().plusDays(2));

                Product p4 = pm.createProduct(105, "Cookie", BigDecimal.valueOf(3.99),
                                Rating.TWO_STAR, LocalDate.now());

                Product p5 = p3.applyRating(Rating.FOUR_STAR);

                Product p6 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99),
                                Rating.FIVE_STAR);
                Product p7 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99),
                                Rating.FIVE_STAR, LocalDate.now().plusDays(2));

                Product p8 = p4.applyRating(Rating.FIVE_STAR);
                // Product p9 = p1.applyRating(Rating.THREE_STAR);

                pm.printProducts(p -> true, priceSorter);
                pm.printProducts(p -> true, ratingSorter);
                pm.printProducts(p -> true, ratingSorter.thenComparing(priceSorter));

                Map<String, String> mp = pm.getDiscounts();
                mp.keySet().stream().forEach(key -> System.out.println(key + " " + mp.get(key)));

        }
}
