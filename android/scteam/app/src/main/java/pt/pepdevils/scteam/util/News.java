package pt.pepdevils.scteam.util;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by PepDevils on 15/02/2017.
 */

public class News implements Serializable {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("post_date")
    @Expose
    private String postDate;
    @SerializedName("post_content")
    @Expose
    private String postContent;
    @SerializedName("post_title")
    @Expose
    private String postTitle;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("galeria")
    @Expose
    private List<String> galeria = null;

    public News() {}

    public News(String postDate, String postTitle, String postContent, String image, String video, List<String> galeria) {
        System.out.println("entrei news; " + postDate + "post title" +  postTitle);
        this.postDate = postDate;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.image = image;
        this.video = video;
        this.galeria = galeria;
    }

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public List<String> getGaleria() {
        return galeria;
    }

    public void setGaleria(List<String> galeria) {
        this.galeria = galeria;
    }
}