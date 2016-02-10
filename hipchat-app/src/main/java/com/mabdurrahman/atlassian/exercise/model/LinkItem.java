package com.mabdurrahman.atlassian.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class LinkItem extends BaseObject {

    private static final long serialVersionUID = -6499054337757407445L;

    @JsonProperty
    private String url;

    @JsonProperty
    private String title;

    public LinkItem() {
    }

    public LinkItem(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
