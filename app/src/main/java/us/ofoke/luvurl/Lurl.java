package us.ofoke.luvurl;

/**
 * Created by Art on 5/31/2016.
 */
public class Lurl {
    public int luvrating;
    public int noluvrating;
    public long timestamp;
    public String url;

    public Lurl(){
    }

    public Lurl(int luvrating, int noluvrating, long timestamp, String url){
        this.luvrating = luvrating;
        this.noluvrating = noluvrating;
        this.timestamp = timestamp;
        this.url = url;
    }

//    public Double getLuvRating(){
//        return luvrating;
//    }

/*    public void setLuvrating(Double luvrating) {
        this.luvrating = luvrating;
    }*/

/*    public Double getNoLuvRating(){
        return noluvrating;
    }*/

/*    public void setNoluvrating(Double noluvrating) {
        this.noluvrating = noluvrating;
    }*/

/*    public Long getTimestamp(){
        return timestamp;
    }*/

/*    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }*/

    public String getUrl(){
        return url;
    }

/*    public void setUrl(String url) {
        this.url = url;
    }*/
}
