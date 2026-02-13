/*
 * Add copyright here.
 */

package labs.pm.app;

import labs.pm.data.Drink;
import labs.pm.data.Food;
import labs.pm.data.Product;
import labs.pm.data.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author vishalkushwaha
 * @version 1.0
 * {@code Shop} class represents an application that manages products.
 **/
public class Shop {
    static void main() {
        Product p1 = new Drink(101, "Tea", BigDecimal.valueOf(1.99), Rating.THREE_STAR);
        System.out.println(p1);

        Product p2 = new Drink(102, "Coffee", BigDecimal.valueOf(1.99), Rating.FOUR_STAR);
        System.out.println(p2);

        Product p3 = new Food(103, "Cake", BigDecimal.valueOf(3.99), Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        System.out.println(p3);

        Product p4 = new Food(105, "Cookie", BigDecimal.valueOf(3.99), Rating.TWO_STAR, LocalDate.now());
        System.out.println(p4);

        Product p5 = p3.applyRating(Rating.FOUR_STAR);
        System.out.println(p5);

        Product p6 = new Drink(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR);
        Product p7 = new Food(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        System.out.println(p6.equals(p7));

        Product p8 = p4.applyRating(Rating.FIVE_STAR);
        Product p9 = p1.applyRating(Rating.TWO_STAR);

        System.out.println(p8);
        System.out.println(p1);

        System.out.println(p3.getBestBefore());

    }
}
