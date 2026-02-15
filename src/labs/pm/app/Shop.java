/*
 * Add copyright here.
 */

package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

/**
 * @author vishalkushwaha
 * @version 1.0
 * {@code Shop} class represents an application that manages products.
 **/
public class Shop {
    static void main() {

        ProductManager pm = new ProductManager(Locale.UK);

        Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.printProductReport();
        p1 = pm.reviewProduct(p1,Rating.FOUR_STAR, "Nice hot cup of tea!");
        pm.printProductReport();

//        Product p2 = pm.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), Rating.FOUR_STAR);
//        System.out.println(p2);
//
//        Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99), Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//        System.out.println(p3);
//
//        Product p4 = pm.createProduct(105, "Cookie", BigDecimal.valueOf(3.99), Rating.TWO_STAR, LocalDate.now());
//        System.out.println(p4);
//
//        Product p5 = p3.applyRating(Rating.FOUR_STAR);
//        System.out.println(p5);
//
//        Product p6 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR);
//        Product p7 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//        System.out.println(p6.equals(p7));
//
//        Product p8 = p4.applyRating(Rating.FIVE_STAR);
//        Product p9 = p1.applyRating(Rating.TWO_STAR);
//
//        System.out.println(p8);
//        System.out.println(p1);
//
//        System.out.println(p3.getBestBefore());

    }
}
