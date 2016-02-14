package com.mabdurrahman.atlassian.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/9/16.
 */
@JsonRootName("query")
public class YahooYqlResponse extends BaseObject {

    private static final long serialVersionUID = -4682628859338119539L;

    @JsonProperty
    private int count;

    @JsonProperty
    private Date created;

    @JsonProperty
    private String lang;

    @JsonProperty("results")
    private TitleResult titleResult;

    public YahooYqlResponse() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public TitleResult getTitleResult() {
        return titleResult;
    }

    public void setTitleResult(TitleResult titleResult) {
        this.titleResult = titleResult;
    }

    public static class TitleResult {

        @JsonProperty
        private String title;

        public TitleResult() {
        }

        public TitleResult(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
