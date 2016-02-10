package com.mabdurrahman.atlassian.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class MessageItem extends BaseObject {

    private static final long serialVersionUID = 2401554129441836178L;

    @JsonIgnore
    private String rawMessage;

    @JsonProperty
    private List<String> mentions;

    @JsonProperty
    private List<String> emoticons;

    @JsonProperty
    private List<LinkItem> links;

    @JsonIgnore
    private List<ContentEntity> allEntities;

    public MessageItem() {

    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        if (mentions == null || mentions.isEmpty()) return;

        this.mentions = mentions;
    }

    public List<String> getEmoticons() {
        return emoticons;
    }

    public void setEmoticons(List<String> emoticons) {
        if (emoticons == null || emoticons.isEmpty()) return;

        this.emoticons = emoticons;
    }

    public List<LinkItem> getLinks() {
        return links;
    }

    public void setLinks(List<LinkItem> links) {
        if (links == null || links.isEmpty()) return;

        this.links = links;
    }

    public List<ContentEntity> getAllEntities() {
        if (allEntities == null) {
            allEntities = Collections.emptyList();
        }
        return allEntities;
    }

    public void setAllEntities(List<ContentEntity> allEntities) {
        this.allEntities = allEntities;
    }
}
