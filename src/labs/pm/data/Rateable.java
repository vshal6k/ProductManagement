/*
 * Add copyright here.
 */

package labs.pm.data;

/**
 * @author vishalkushwaha
 **/
@FunctionalInterface
public interface Rateable<T> {
    public static final Rating DEAFULT_RATING = Rating.NOT_RATED;

    public abstract T applyRating(Rating rating);

    public default T applyRating(int stars){
        Rating rating = convert(stars);
        return applyRating(rating);
    }

    default Rating getRating(){
        return DEAFULT_RATING;
    }

    public static Rating convert(int stars){
        return (stars >= 0 && stars <= 5) ? Rating.values()[stars] : DEAFULT_RATING;
    }

}
