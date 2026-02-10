/*
 * Add copyright here.
 */

package labs.pm.data;

import java.math.BigDecimal;
import static java.math.RoundingMode.HALF_UP;

/**
 * @author vishalkushwaha
 * @version 1.0
 * {@code Product} class represents properties and behaviors of product objects in the product management system.
 * <br>
 * Each product has id, name and price.
 * <br>
 * Each product can have a discount calculated based on discount rate.
 **/
public class Product {
    private int id;
    private String name;
    private BigDecimal price;
    private Rating rating;
    /**
     * A constant that defines a {@link java.math.BigDecimal BigDecimal} value of the discount rate
     */
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount(){
        return this.price.multiply(DISCOUNT_RATE).setScale(2, HALF_UP);
    }

    public Rating getRating() {
        return rating;
    }

    public Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public Product(int id, String name, BigDecimal price) {
        this(id, name, price, Rating.UNRATED);
    }

    public Product(){
        this(0, "no name", BigDecimal.ZERO, Rating.UNRATED);
    }

}
