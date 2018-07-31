package com.pekebyte.somosoco.data.models;

/**
 * Created by pedromolina on 2/4/18.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OcoPosts {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("nextPageToken")
    @Expose
    private String nextPageToken;
    @SerializedName("posts")
    @Expose
    private List<Post> posts = null;
    @SerializedName("etag")
    @Expose
    private String etag;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

}
