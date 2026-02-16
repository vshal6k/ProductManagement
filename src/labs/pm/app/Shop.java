/*
 * Add copyright here.
 */

package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author vishalkushwaha
 * @version 1.0
 *          {@code Shop} class represents an application that manages products.
 **/
public class Shop {

    public static void main(String[] args) {

        ProductManager pm = new ProductManager("en-GB");

        Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);

        pm.reviewProduct(101, Rating.FOUR_STAR, "Nice hot cup of tea!");
        pm.reviewProduct(101, Rating.TWO_STAR, "Rather weak tea");
        pm.reviewProduct(101, Rating.FOUR_STAR, "Fine tea");
        pm.reviewProduct(101, Rating.FOUR_STAR, "Good tea");
        pm.reviewProduct(101, Rating.FIVE_STAR, "Perfect tea");
        pm.reviewProduct(101, Rating.THREE_STAR, "Just add some lemon");
        // pm.printProductReport(101);

        // pm.changeLocale("ru-RU");

        Product p2 = pm.createProduct(102, "Coffee", BigDecimal.valueOf(4.99), Rating.FOUR_STAR);
        pm.reviewProduct(102, Rating.TWO_STAR, "Good tea");
        pm.reviewProduct(102, Rating.THREE_STAR, "Good tea");
        pm.reviewProduct(102, Rating.ONE_STAR, "Nice hot cup of tea!");
        pm.reviewProduct(102, Rating.TWO_STAR, "Good tea");
        pm.reviewProduct(102, Rating.THREE_STAR, "Good tea");

        Comparator<Product> ratingSorter = (p3, p4) -> p4.getRating().ordinal() - p3.getRating().ordinal();
        Comparator<Product> priceSorter = (p3, p4) -> p3.getPrice().compareTo(p4.getPrice());
        // pm.printProductReport(102);

        // System.out.println(p2);
        //
        Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99),
        Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        // System.out.println(p3);
        //
        Product p4 = pm.createProduct(105, "Cookie", BigDecimal.valueOf(3.99),
        Rating.TWO_STAR, LocalDate.now());
        // System.out.println(p4);
        //
        Product p5 = p3.applyRating(Rating.FOUR_STAR);
        // System.out.println(p5);
        //
        Product p6 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99),
        Rating.FIVE_STAR);
        Product p7 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99),
        Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        // System.out.println(p6.equals(p7));
        //
        // Product p8 = p4.applyRating(Rating.FIVE_STAR);
        // Product p9 = p1.applyRating(Rating.TWO_STAR);
        //
        // System.out.println(p8);
        // System.out.println(p1);
        //
        // System.out.println(p3.getBestBefore());

        pm.printProducts(p -> p.getPrice().floatValue() < 2 ,priceSorter);
        pm.printProducts(p -> p.getPrice().floatValue() < 2 ,ratingSorter);
        pm.printProducts(p -> p.getPrice().floatValue() < 2 ,ratingSorter.thenComparing(priceSorter));


    }
}
