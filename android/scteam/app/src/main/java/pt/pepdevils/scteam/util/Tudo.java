package pt.pepdevils.scteam.util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by PepDevils on 31/01/2017.
 */

public class Tudo implements Serializable {

    @SerializedName("url_tv")
    @Expose
    private String urlTv;
    @SerializedName("news")
    @Expose
    private List<News> news = null;

    public String getUrlTv() {
        return urlTv;
    }

    public void setUrlTv(String urlTv) {
        this.urlTv = urlTv;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

}

