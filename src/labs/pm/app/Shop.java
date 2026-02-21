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

                pm.printProductReport(101);

                pm.changeLocale("ru-RU");

                pm.printProductReport(102);
                
                pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
                
                pm.reviewProduct(103, Rating.THREE_STAR, "Soft and Fresh Cake");

                pm.reviewProduct(103, Rating.FIVE_STAR, "Loved the taste");

                pm.reviewProduct(103, Rating.TWO_STAR, "Base can be better!");
                
                pm.printProductReport(103);

                Comparator<Product> ratingSorter = (x1, x2) -> x2.getRating().ordinal() -
                                x1.getRating().ordinal();
                
                Comparator<Product> priceSorter = (x1, x2) -> x1.getPrice().compareTo(x2.getPrice());
                
                pm.printProducts(p -> true, priceSorter);
                pm.printProducts(p -> true, ratingSorter);
                pm.printProducts(p -> true, ratingSorter.thenComparing(priceSorter));

        }
}
