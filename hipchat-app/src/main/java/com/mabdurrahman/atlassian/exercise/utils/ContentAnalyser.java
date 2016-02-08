package com.mabdurrahman.atlassian.exercise.utils;

import com.mabdurrahman.atlassian.exercise.model.ContentEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class ContentAnalyser {

    /**
     * Extract @mentions, {emoticons}, and URLs from a given message text.
     * @param text text of tweet
     * @return list of extracted entities values
     */
    public List<String> extractEntities(String text) {
        List<ContentEntity> entities = extractEntitiesWithIndices(text);

        List<String> flatEntities = new ArrayList<>();
        for (ContentEntity entity : entities) {
            flatEntities.add(entity.getValue());
        }

        return flatEntities;
    }

    /**
     * Extract @mentions, {emoticons}, and URLs from a given message text.
     * @param text text of tweet
     * @return list of extracted entities
     */
    public List<ContentEntity> extractEntitiesWithIndices(String text) {
        List<ContentEntity> entities = new ArrayList<>();
        entities.addAll(extractMentionsWithIndices(text));
        entities.addAll(extractEmoticonsWithIndices(text));
        entities.addAll(extractURLsWithIndices(text));

        return entities;
    }

    /**
     * Extract @mention references from a given text. A mention is an occurrence of @mention anywhere in a message text.
     *
     * @param text of the message from which to extract mentions
     * @return List of mentions referenced (without the leading @ sign)
     */
    public List<String> extractMentions(String text) {
        // TODO Implement the logic
        return Collections.emptyList();
    }

    /**
     * Extract @mention references from a given text. A mention is an occurrence of @mention anywhere in a message text.
     *
     * @param text of the message from which to extract mentions
     * @return List of {@link ContentEntity} of type {@link ContentEntity.Type#MENTION}, having info
     *  about start index, end index, and value of the referenced mention (without the leading @ sign)
     */
    public List<ContentEntity> extractMentionsWithIndices(String text) {
        // TODO Implement the logic
        return Collections.emptyList();
    }

    /**
     * Extract (emoticons) references from a given text. An emoticon is an occurrence of (emoticon) anywhere in a message text.
     *
     * @param text of the message from which to extract emoticons
     * @return List of emoticons referenced (without the wrapping () parentheses)
     */
    public List<String> extractEmoticons(String text) {
        // TODO Implement the logic
        return Collections.emptyList();
    }

    /**
     * Extract (emoticons) references from a given text. An emoticon is an occurrence of (emoticon) anywhere in a message text.
     *
     * @param text of the message from which to extract emoticons
     * @return List of {@link ContentEntity} of type {@link ContentEntity.Type#EMOTICON}, having info
     *  about start index, end index, and value of the referenced emoticon (without the wrapping () parentheses)
     */
    public List<ContentEntity> extractEmoticonsWithIndices(String text) {
        // TODO Implement the logic
        return Collections.emptyList();
    }

    /**
     * Extract URL references from a given text.
     *
     * @param text of the message from which to extract URLs
     * @return List of URLs referenced.
     */
    public List<String> extractURLs(String text) {
        // TODO Implement the logic
        return Collections.emptyList();
    }

    /**
     * Extract URL references from a given text.
     *
     * @param text of the message from which to extract URLs
     * @return List of {@link ContentEntity} of type {@link ContentEntity.Type#URL}, having info
     *  about start index, end index, and value of the referenced URL
     */
    public List<ContentEntity> extractURLsWithIndices(String text) {
        // TODO Implement the logic
        return Collections.emptyList();
    }
}
