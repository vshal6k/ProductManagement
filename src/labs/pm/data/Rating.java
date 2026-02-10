/*
 * Add copyright here.
 */

package labs.pm.data;

/**
 * @author vishalkushwaha
 **/
public enum Rating {
    ONE_STAR("★☆☆☆☆"), TWO_STAR("★★☆☆☆"),
    THREE_STAR("★★★☆☆"), FOUR_STAR("★★★★☆"),
    FIVE_STAR("★★★★★"), UNRATED("☆☆☆☆☆");

    private final String stars;

    Rating(String stars) {
        this.stars = stars;
    }

    public String getStars() {
        return stars;
    }
}
