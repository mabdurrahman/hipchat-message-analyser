package com.mabdurrahman.atlassian.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class MessageItem extends BaseObject {

    @JsonProperty
    private List<String> mentions;

    @JsonProperty
    private List<String> emoticons;

    @JsonProperty
    private List<LinkItem> links;

    public MessageItem() {

    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public List<String> getEmoticons() {
        return emoticons;
    }

    public void setEmoticons(List<String> emoticons) {
        this.emoticons = emoticons;
    }

    public List<LinkItem> getLinks() {
        return links;
    }

    public void setLinks(List<LinkItem> links) {
        this.links = links;
    }
}
