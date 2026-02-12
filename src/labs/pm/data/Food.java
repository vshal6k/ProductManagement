/*
 * Add copyright here.
 */

package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author vishalkushwaha
 **/
public class Food extends Product{
    private LocalDate bestBefore;

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        super(id, name, price, rating);
        this.bestBefore = bestBefore;
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.bestBefore;
    }
}
