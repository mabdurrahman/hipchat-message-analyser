package com.mabdurrahman.atlassian.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/9/16.
 */
public class ApiURLTitle extends BaseObject {

    private static final long serialVersionUID = 533355664202610017L;

    @JsonProperty
    private ApiStatus status;

    @JsonProperty
    private String url;

    @JsonProperty
    private String title;

    @JsonProperty
    private String statusInfo;

    public ApiURLTitle() {
    }

    public ApiStatus getStatus() {
        return status;
    }

    public void setStatus(ApiStatus status) {
        this.status = status;
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

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }
}
