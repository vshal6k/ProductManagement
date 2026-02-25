/*
 * Add copyright here.
 */

package labs.pm.data;

import java.io.Serializable;

/**
 * @author vishalkushwaha
 **/
public record Review(Rating rating, String comments) implements Comparable<Review>, Serializable {
    @Override
    public int compareTo(Review o) {
        return this.rating.ordinal() - o.rating.ordinal();
    }
}
