package us.ofoke.luvurl;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Art on 5/31/2016.
 */
@IgnoreExtraProperties
public class Lurl {
    private int luvRating;
    private int noLuvRating;
    public long timestamp;
    public String url;

    public Lurl() {
    }

    public Lurl(int luvRating, int noLuvRating, long timestamp, String url) {
        this.luvRating = luvRating;
        this.noLuvRating = noLuvRating;
        this.timestamp = timestamp;
        this.url = url;
    }

    public int getLuvRating() {
        return luvRating;
    }

/*    public void setLuvrating(int luvRating) {
        this.luvRating = luvRating;
    }*/

    public int getNoLuvRating() {
        return noLuvRating;
    }

/*    public void setNoluvrating(Double noluvrating) {
        this.noluvrating = noluvrating;
    }*/

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

/*    public void setUrl(String url) {
        this.url = url;
    }*/


//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("luvRating", luvRating);
//        result.put("noluvrating", noluvrating);
//        result.put("timestamp", timestamp);
//        result.put("url", url);
//
//        return result;
//    }
}
