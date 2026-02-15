/*
 * Add copyright here.
 */

package labs.pm.data;

/**
 * @author vishalkushwaha
 **/
public record Review(Rating rating, String comments) implements Comparable<Review>{
    @Override
    public int compareTo(Review o) {
        return this.rating.ordinal() - o.rating.ordinal();
    }
}
