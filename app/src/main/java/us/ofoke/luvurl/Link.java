package us.ofoke.luvurl;

/**
 * Created by Art on 5/31/2016.
 */
public class Link {
    String guid;  // String uniqueID = UUID.randomUUID().toString();
    int rating;
    long timestamp;
    String url;

    public Link(){
    }

    public Link(String guid, int rating, long timestamp, String url){
        this.guid = guid;
        this.rating = rating;
        this.timestamp = timestamp;
        this.url = url;
    }

    public String getGuid(){
        return guid;
    }

    public int getRating(){
        return rating;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getUrl(){
        return url;
    }
}
