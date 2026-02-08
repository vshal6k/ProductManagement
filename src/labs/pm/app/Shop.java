/*
 * Add copyright here.
 */

package labs.pm.app;

import labs.pm.data.Product;

import java.math.BigDecimal;

/**
 * @author vishalkushwaha
 **/
public class Shop {
    static void main() {
        Product p1 = new Product();
        p1.setId(101);
        p1.setName("Tea");
        p1.setPrice(BigDecimal.valueOf(1.99));
        System.out.println(p1.getId() + " " + p1.getName() + " " + p1.getPrice());
    }
}
