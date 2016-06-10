package us.ofoke.luvurl;

/**
 * Created by Art on 5/31/2016.
 */
public class Lurl {
    //String guid;  // String uniqueID = UUID.randomUUID().toString();
    int noluvrating;
    int luvrating;
    long timestamp;
    String url;

    public Lurl(){
    }

    public Lurl(int rating, long timestamp, String url){
       // this.guid = guid;
        this.noluvrating = noluvrating;
        this.luvrating = luvrating;
        this.timestamp = timestamp;
        this.url = url;
    }

/*    public String getGuid(){
        return guid;
    }*/
    public int getNoLuvRating(){
    return noluvrating;
}

    public int getLuvRating(){
        return luvrating;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getUrl(){
        return url;
    }
}
