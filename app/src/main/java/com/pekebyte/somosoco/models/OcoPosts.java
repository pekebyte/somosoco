package com.pekebyte.somosoco.models;

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
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

}
