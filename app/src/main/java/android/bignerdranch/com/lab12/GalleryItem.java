package android.bignerdranch.com.lab12;

/**
 * Created by Peter on 4/25/2016.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private double mLat;
    private double mLon;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public double getLat() {
        return mLat;
    }
    public void setLat(double lat) {
        mLat = lat;
    }
    public double getLon() {
        return mLon;
    }
    public void setLon(double lon) {
        mLon = lon;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
    @Override
    public String toString() {
        return mCaption;
    }
}
