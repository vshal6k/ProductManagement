/*
 * Add copyright here.
 */

package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author vishalkushwaha
 **/
public final class Food extends Product {
    private LocalDate bestBefore;

    public Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        super(id, name, price, rating);
        this.bestBefore = bestBefore;
    }

    @Override
    public Product applyRating(Rating newRating) {
        return new Food(this.getId(), this.getName(), this.getPrice(), newRating, this.getBestBefore());
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }
}
