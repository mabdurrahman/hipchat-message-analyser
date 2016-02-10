package com.mabdurrahman.atlassian.exercise.utils;

import com.mabdurrahman.atlassian.exercise.model.ContentEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class ContentAnalyser {

    private static ContentAnalyser instance;

    public static ContentAnalyser getInstance() {
        if (instance == null) {
            instance = new ContentAnalyser();
        }
        return instance;
    }

    private ContentAnalyser() {

    }

    /**
     * Extract @mentions, {emoticons}, and URLs from a given message text.
     * @param text text of message
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
     * @param text text of message
     * @return an Observable that emits all entities extracted as a list
     */
    public Observable<List<String>> rxExtractEntities(final String text) {
        return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override
            public Observable<List<String>> call() {
                return Observable.just(extractEntities(text));
            }
        });
    }

    /**
     * Extract @mentions, {emoticons}, and URLs from a given message text.
     * @param text text of message
     * @return list of extracted entities
     */
    public List<ContentEntity> extractEntitiesWithIndices(String text) {
        List<ContentEntity> entities = new ArrayList<>();
        entities.addAll(extractMentionsWithIndices(text));
        entities.addAll(extractEmoticonsWithIndices(text));
        entities.addAll(extractURLsWithIndices(text));

        removeOverlappingEntities(entities);
        return entities;
    }

    /**
     * Extract @mentions, {emoticons}, and URLs from a given message text.
     * @param text text of message
     * @return an Observable that emits all entities extracted as a list
     */
    public Observable<List<ContentEntity>> rxExtractEntitiesWithIndices(final String text) {
        return Observable.defer(new Func0<Observable<List<ContentEntity>>>() {
            @Override
            public Observable<List<ContentEntity>> call() {
                return Observable.just(extractEntitiesWithIndices(text));
            }
        });
    }

    /**
     * Extract @mention references from a given text. A mention is an occurrence of @mention anywhere in a message text.
     *
     * @param text of the message from which to extract mentions
     * @return List of mentions referenced (without the leading @ sign)
     */
    public List<String> extractMentions(String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }

        List<String> extracted = new ArrayList<>();
        for (ContentEntity entity : extractMentionsWithIndices(text)) {
            extracted.add(entity.getValue());
        }
        return extracted;
    }

    /**
     * Extract @mention references from a given text. A mention is an occurrence of @mention anywhere in a message text.
     *
     * @param text of the message from which to extract mentions
     * @return an Observable that emits all mentions referenced (without the leading @ sign)
     */
    public Observable<List<String>> rxExtractMentions(final String text) {
        return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override
            public Observable<List<String>> call() {
                return Observable.just(extractMentions(text));
            }
        });
    }

    /**
     * Extract @mention references from a given text. A mention is an occurrence of @mention anywhere in a message text.
     *
     * @param text of the message from which to extract mentions
     * @return List of {@link ContentEntity} of type {@link ContentEntity.Type#MENTION}, having info
     *  about start index, end index, and value of the referenced mention (without the leading @ sign)
     */
    public List<ContentEntity> extractMentionsWithIndices(String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }

        // Performance optimization.
        // If text doesn't contain @ at all, the text doesn't
        // contain @mention. So we can simply return an empty list.
        boolean found = false;
        for (char c : text.toCharArray()) {
            if (c == '@') {
                found = true;
                break;
            }
        }
        if (!found) {
            return Collections.emptyList();
        }

        List<ContentEntity> extracted = new ArrayList<>();
        Matcher matcher = ContentRegex.VALID_MENTION.matcher(text);
        while (matcher.find()) {
            if (matcher.group(ContentRegex.VALID_MENTION_GROUP_USERNAME) == null) {
                continue;
            }

            extracted.add(new ContentEntity(matcher, ContentEntity.Type.MENTION, ContentRegex.VALID_MENTION_GROUP_USERNAME));
        }
        return extracted;
    }

    /**
     * Extract @mention references from a given text. A mention is an occurrence of @mention anywhere in a message text.
     *
     * @param text of the message from which to extract mentions
     * @return an Observable that emits {@link ContentEntity}(s) of type {@link ContentEntity.Type#MENTION},
     *  having info about start index, end index, and value of the referenced mention (without the leading
     *  @ sign)
     */
    public Observable<List<ContentEntity>> rxExtractMentionsWithIndices(final String text) {
        return Observable.defer(new Func0<Observable<List<ContentEntity>>>() {
            @Override
            public Observable<List<ContentEntity>> call() {
                return Observable.just(extractMentionsWithIndices(text));
            }
        });
    }

    /**
     * Extract (emoticons) references from a given text. An emoticon is an occurrence of (emoticon) anywhere in a message text.
     *
     * @param text of the message from which to extract emoticons
     * @return List of emoticons referenced (without the wrapping () parentheses)
     */
    public List<String> extractEmoticons(String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }

        List<String> extracted = new ArrayList<>();
        for (ContentEntity entity : extractEmoticonsWithIndices(text)) {
            extracted.add(entity.getValue());
        }
        return extracted;
    }

    /**
     * Extract (emoticons) references from a given text. An emoticon is an occurrence of (emoticon) anywhere in a message text.
     *
     * @param text of the message from which to extract emoticons
     * @return an Observable that emits all emoticons referenced (without the wrapping () parentheses)
     */
    public Observable<List<String>> rxExtractEmoticons(final String text) {
        return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override
            public Observable<List<String>> call() {
                return Observable.just(extractEmoticons(text));
            }
        });
    }

    /**
     * Extract (emoticons) references from a given text. An emoticon is an occurrence of (emoticon) anywhere in a message text.
     *
     * @param text of the message from which to extract emoticons
     * @return List of {@link ContentEntity} of type {@link ContentEntity.Type#EMOTICON}, having info
     *  about start index, end index, and value of the referenced emoticon (without the wrapping () parentheses)
     */
    public List<ContentEntity> extractEmoticonsWithIndices(String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }

        // Performance optimization.
        // If text doesn't contain both ( and ) at all, the text doesn't
        // contain (emoticon). So we can simply return an empty list.
        boolean openingFound = false;
        boolean closingFound = false;
        for (char c : text.toCharArray()) {
            if (c == '(') {
                openingFound = true;
            } else if (openingFound && c == ')') {
                closingFound = true;
                break;
            }
        }
        if (!closingFound) {
            return Collections.emptyList();
        }

        List<ContentEntity> extracted = new ArrayList<>();
        Matcher matcher = ContentRegex.VALID_EMOTICON.matcher(text);
        while (matcher.find()) {
            String emoticon = matcher.group(ContentRegex.VALID_EMOTICON_GROUP_VALUE);
            int start = matcher.start(ContentRegex.VALID_EMOTICON_GROUP_LEFT_PAREN);
            int end = matcher.end(ContentRegex.VALID_EMOTICON_GROUP_RIGHT_PAREN);

            extracted.add(new ContentEntity(start, end, emoticon, ContentEntity.Type.EMOTICON));
        }
        return extracted;
    }

    /**
     * Extract (emoticons) references from a given text. An emoticon is an occurrence of (emoticon) anywhere in a message text.
     *
     * @param text of the message from which to extract emoticons
     * @return an Observable that emits {@link ContentEntity}(s) of type {@link ContentEntity.Type#EMOTICON},
     *  having info about start index, end index, and value of the referenced emoticon (without the wrapping
     *  () parentheses)
     */
    public Observable<List<ContentEntity>> rxExtractEmoticonsWithIndices(final String text) {
        return Observable.defer(new Func0<Observable<List<ContentEntity>>>() {
            @Override
            public Observable<List<ContentEntity>> call() {
                return Observable.just(extractEmoticonsWithIndices(text));
            }
        });
    }

    /**
     * Extract URL references from a given text.
     *
     * @param text of the message from which to extract URLs
     * @return List of URLs referenced.
     */
    public List<String> extractURLs(String text) {
        if (text == null || text.length() == 0) {
            return Collections.emptyList();
        }

        List<String> urls = new ArrayList<>();
        for (ContentEntity entity : extractURLsWithIndices(text)) {
            urls.add(entity.getValue());
        }
        return urls;
    }

    /**
     * Extract URL references from a given text.
     *
     * @param text of the message from which to extract URLs
     * @return an Observable that emits all URLs referenced.
     */
    public Observable<List<String>> rxExtractURLs(final String text) {
        return Observable.defer(new Func0<Observable<List<String>>>() {
            @Override
            public Observable<List<String>> call() {
                return Observable.just(extractURLs(text));
            }
        });
    }

    /**
     * Extract URL references from a given text.
     *
     * @param text of the message from which to extract URLs
     * @return List of {@link ContentEntity} of type {@link ContentEntity.Type#URL}, having info
     *  about start index, end index, and value of the referenced URL
     */
    public List<ContentEntity> extractURLsWithIndices(String text) {
        if (text == null || text.length() == 0 || text.indexOf('.') == -1) {
            // Performance optimization.
            // If text doesn't contain '.' or ':' at all, text doesn't contain URL,
            // so we can simply return an empty list.
            return Collections.emptyList();
        }

        List<ContentEntity> urls = new ArrayList<>();

        Matcher matcher = ContentRegex.VALID_URL.matcher(text);
        while (matcher.find()) {
            if (matcher.group(ContentRegex.VALID_URL_GROUP_PROTOCOL) == null) {
                // skip if URL is preceded by invalid character.
                if (ContentRegex.INVALID_URL_WITHOUT_PROTOCOL_MATCH_BEGIN
                        .matcher(matcher.group(ContentRegex.VALID_URL_GROUP_BEFORE)).matches()) {
                    continue;
                }
            }
            String url = matcher.group(ContentRegex.VALID_URL_GROUP_URL);
            int start = matcher.start(ContentRegex.VALID_URL_GROUP_URL);
            int end = matcher.end(ContentRegex.VALID_URL_GROUP_URL);

            urls.add(new ContentEntity(start, end, url, ContentEntity.Type.URL));
        }

        return urls;
    }

    /**
     * Extract URL references from a given text.
     *
     * @param text of the message from which to extract URLs
     * @return an Observable that emits {@link ContentEntity}(s) of type {@link ContentEntity.Type#URL},
     *  having info about start index, end index, and value of the referenced URL
     */
    public Observable<List<ContentEntity>> rxExtractURLsWithIndices(final String text) {
        return Observable.defer(new Func0<Observable<List<ContentEntity>>>() {
            @Override
            public Observable<List<ContentEntity>> call() {
                return Observable.just(extractURLsWithIndices(text));
            }
        });
    }

    private void removeOverlappingEntities(List<ContentEntity> entities) {
        // sort by index
        Collections.sort(entities, new Comparator<ContentEntity>() {
            public int compare(ContentEntity e1, ContentEntity e2) {
                return e1.getStart() - e2.getStart();
            }
        });

        // Remove overlapping entities.
        // Two entities overlap only when one is URL and the other is mention/emoticon
        // which is a part of the URL. When it happens, we choose URL over mention/emoticon
        // by selecting the one with smaller start index.
        if (!entities.isEmpty()) {
            Iterator<ContentEntity> it = entities.iterator();
            ContentEntity prev = it.next();
            while (it.hasNext()) {
                ContentEntity cur = it.next();
                if (prev.getEnd() > cur.getStart()) {
                    it.remove();
                } else {
                    prev = cur;
                }
            }
        }
    }
}
