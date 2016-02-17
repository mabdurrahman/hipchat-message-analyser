package com.mabdurrahman.atlassian.exercise.manager;

import com.mabdurrahman.atlassian.exercise.api.RestDataSource;
import com.mabdurrahman.atlassian.exercise.model.ContentEntity;
import com.mabdurrahman.atlassian.exercise.model.LinkItem;
import com.mabdurrahman.atlassian.exercise.model.MessageItem;
import com.mabdurrahman.atlassian.exercise.utils.ContentAnalyser;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func4;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/16/16.
 */
public class ChatManager {

    private static ChatManager instance;

    private Observable<MessageItem> messageAnalyserRequestCache = null;

    private ChatManager() {

    }

    public static final ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }

        return instance;
    }

    public Observable<MessageItem> getMessageAnalyserRequestCache() {
        return messageAnalyserRequestCache;
    }

    public Observable<MessageItem> analyseChatMessage(final String messageText) {

        if (messageAnalyserRequestCache == null) {
            messageAnalyserRequestCache = ContentAnalyser.getInstance().rxExtractEntitiesWithIndices(messageText)

                    .flatMap(new Func1<List<ContentEntity>, Observable<MessageItem>>() {
                        @Override
                        public Observable<MessageItem> call(List<ContentEntity> contentEntities) {

                            Observable<ContentEntity> entitiesObservable = Observable.from(contentEntities);

                            Observable<List<String>> mentionsObservable = entitiesObservable
                                    .filter(new Func1<ContentEntity, Boolean>() {
                                        @Override
                                        public Boolean call(ContentEntity contentEntity) {
                                            return contentEntity.getType().equals(ContentEntity.Type.MENTION);
                                        }
                                    })
                                    .map(new Func1<ContentEntity, String>() {
                                        @Override
                                        public String call(ContentEntity contentEntity) {
                                            return contentEntity.getValue();
                                        }
                                    }).toList();

                            Observable<List<String>> emoticonsObservable = entitiesObservable
                                    .filter(new Func1<ContentEntity, Boolean>() {
                                        @Override
                                        public Boolean call(ContentEntity contentEntity) {
                                            return contentEntity.getType().equals(ContentEntity.Type.EMOTICON);
                                        }
                                    })
                                    .map(new Func1<ContentEntity, String>() {
                                        @Override
                                        public String call(ContentEntity contentEntity) {
                                            return contentEntity.getValue();
                                        }
                                    }).toList();

                            Observable<List<LinkItem>> linksObservable = entitiesObservable
                                    .filter(new Func1<ContentEntity, Boolean>() {
                                        @Override
                                        public Boolean call(ContentEntity contentEntity) {
                                            return contentEntity.getType().equals(ContentEntity.Type.URL);
                                        }
                                    })
                                    .flatMap(new Func1<ContentEntity, Observable<LinkItem>>() {
                                        @Override
                                        public Observable<LinkItem> call(ContentEntity contentEntity) {
                                            return new RestDataSource().getURLTitle(contentEntity.getValue());
                                        }
                                    }).toList();

                            // compose these together
                            return Observable.zip(mentionsObservable,
                                    emoticonsObservable,
                                    linksObservable,
                                    Observable.just(contentEntities),
                                    new Func4<List<String>, List<String>, List<LinkItem>, List<ContentEntity>, MessageItem>() {
                                        @Override
                                        public MessageItem call(List<String> mentions, List<String> emoticons, List<LinkItem> linkItems, List<ContentEntity> contentEntities) {

                                            MessageItem messageItem = new MessageItem();

                                            messageItem.setRawMessage(messageText);
                                            messageItem.setMentions(mentions);
                                            messageItem.setEmoticons(emoticons);
                                            messageItem.setLinks(linkItems);
                                            messageItem.setAllEntities(contentEntities);

                                            return messageItem;
                                        }
                                    });
                        }
                    })
                    .cache();
        }

        return messageAnalyserRequestCache;
    }

    public void invalidate() {
        messageAnalyserRequestCache = null;
    }
}
