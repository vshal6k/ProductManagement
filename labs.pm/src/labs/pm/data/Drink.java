/*
 * Add copyright here.
 */

package labs.pm.data;

import java.math.BigDecimal;

/**
 * @author vishalkushwaha
 **/
public final class Drink extends Product {
    public Drink(int id, String name, BigDecimal price, Rating rating) {
        super(id, name, price, rating);
    }

    @Override
    public Product applyRating(Rating newRating) {
        return new Drink(this.getId(), this.getName(), this.getPrice(), newRating);
    }
}
