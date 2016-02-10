package com.mabdurrahman.atlassian.exercise.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.EditText;
import android.widget.ImageView;

import com.mabdurrahman.atlassian.exercise.R;
import com.mabdurrahman.atlassian.exercise.adapter.MessagesRecyclerAdapter;
import com.mabdurrahman.atlassian.exercise.api.RestDataSource;
import com.mabdurrahman.atlassian.exercise.model.ChatMessageItem;
import com.mabdurrahman.atlassian.exercise.model.ContentEntity;
import com.mabdurrahman.atlassian.exercise.model.LinkItem;
import com.mabdurrahman.atlassian.exercise.model.MessageItem;
import com.mabdurrahman.atlassian.exercise.utils.ContentAnalyser;
import com.mabdurrahman.atlassian.exercise.utils.InputUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/10/16.
 */
public class ChatActivity extends BasicActivity {

    public static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static final String EXTRA_FOCUS_MESSAGE_EDIT = "EXTRA_FOCUS_MESSAGE_EDIT";

    private static final long UI_UPDATE_PERIOD = 60 * 1000;

    @Bind(R.id.recycler)
    protected RecyclerView recyclerView;

    @Bind(R.id.edit_message)
    protected EditText messageEditText;

    @Bind(R.id.btn_submit)
    protected ImageView mBtnSubmit;

    @State
    protected String username;

    @State
    protected boolean focusMessageEdit;

    @State
    protected ArrayList<ChatMessageItem> chatMessageItems = new ArrayList<>();

    private MessagesRecyclerAdapter messagesRecyclerAdapter;

    private LinearLayoutManager linearLayoutManager;

    private Handler updateHandler;

    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setReverseLayout(true);

        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.divider_list_light), false, false));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        messagesRecyclerAdapter = new MessagesRecyclerAdapter(this, null);
        messagesRecyclerAdapter.setItemsList(chatMessageItems);

        recyclerView.setAdapter(messagesRecyclerAdapter);

        if (focusMessageEdit) {
            focusMessageEdit = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageEditText.requestFocus();
                    InputUtils.showKeyboard(ChatActivity.this, messageEditText);
                }
            }, 500);
        }

        // Delay a little, Simulating a popping up Welcome Message
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addWelcomeMessage();
            }
        }, 1000);

        updateHandler = new Handler();
        updateRunnable = new UpdateLayoutRunnable();
    }

    @Override
    protected int getContentResource() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onFirstLaunch() {
        super.onFirstLaunch();

        if (!getIntent().hasExtra(EXTRA_USERNAME))
            finish();

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        focusMessageEdit = getIntent().getBooleanExtra(EXTRA_FOCUS_MESSAGE_EDIT, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the initial UI runnable
        updateHandler.post(updateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();

        updateHandler.removeCallbacks(updateRunnable);
    }

    private void updateLayout() {
        if (linearLayoutManager == null || messagesRecyclerAdapter == null) return;

        int startPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int endPosition = linearLayoutManager.findLastVisibleItemPosition();

        messagesRecyclerAdapter.notifyItemRangeChanged(startPosition, endPosition - startPosition + 1);
    }

    @OnClick(R.id.btn_submit)
    protected void onSubmitCommentClick() {
        if (messageEditText.getText().toString().length() == 0 || messageEditText.getText().toString().trim().length() == 0) {
            messageEditText.requestFocus();
            return;
        }

        addSelfMessage();
        analyseChatMessage();

        messageEditText.setText("");
        messageEditText.clearFocus();
        InputUtils.hideKeyboard(this);
    }

    private void addWelcomeMessage() {
        if (!chatMessageItems.isEmpty()) return;

        ChatMessageItem welcomeChatMessage = new ChatMessageItem();
        welcomeChatMessage.setSelf(false);
        welcomeChatMessage.setDate(new Date());
        welcomeChatMessage.setFormattedMessage(Html.fromHtml(getString(R.string.label_welcome_message, username)));

        addChatMessage(welcomeChatMessage);
    }

    private void addSelfMessage() {
        String messageText = messageEditText.getText().toString();

        ChatMessageItem selfMessage = new ChatMessageItem();
        selfMessage.setSelf(true);
        selfMessage.setDate(new Date());
        selfMessage.setRawMessage(messageText);

        addChatMessage(selfMessage);
    }

    private void addReplyRawMessage(MessageItem reply) {
        ChatMessageItem replyMessage = new ChatMessageItem();
        replyMessage.setSelf(false);
        replyMessage.setDate(new Date());

        if (reply == null) {
            replyMessage.setRawMessage(getString(R.string.msg_failed_processing_message));
        } else {
            replyMessage.setRawMessage(reply.toString());
        }

        addChatMessage(replyMessage);

        addReplyFormattedMessage(reply);
    }

    private void addReplyFormattedMessage(MessageItem reply) {
        if (reply == null || reply.getAllEntities().isEmpty()) return;

        ChatMessageItem replyMessage = new ChatMessageItem();
        replyMessage.setSelf(false);
        replyMessage.setDate(new Date());

        SpannableString formattedMessage = new SpannableString(reply.getRawMessage());
        for (ContentEntity entity : reply.getAllEntities()) {
            formattedMessage.setSpan(new StyleSpan(Typeface.BOLD), entity.getStart(), entity.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            switch (entity.getType()) {
                case MENTION:
                    formattedMessage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.mention_span_color)), entity.getStart(), entity.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case EMOTICON:
                    formattedMessage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.emoticon_span_color)), entity.getStart(), entity.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case URL:
                    formattedMessage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.url_span_color)), entity.getStart(), entity.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        replyMessage.setFormattedMessage(formattedMessage);

        addChatMessage(replyMessage);
    }

    private void addChatMessage(ChatMessageItem chatMessage) {
        if (chatMessage == null || messagesRecyclerAdapter == null) return;

        chatMessageItems.add(0, chatMessage);
        messagesRecyclerAdapter.notifyItemInserted(0);

        if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
            linearLayoutManager.scrollToPositionWithOffset(0, 20);
            recyclerView.invalidateItemDecorations();
        }
    }

    private void analyseChatMessage() {
        final String messageText = messageEditText.getText().toString();

        ContentAnalyser.getInstance().rxExtractEntitiesWithIndices(messageText)

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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<MessageItem>() {
                            @Override
                            public void call(MessageItem messageItem) {
                                addReplyRawMessage(messageItem);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                addReplyRawMessage(null);
                            }
                        });
    }

    @Override
    public void onBackPressed() {
        if (messageEditText.hasFocus()) {
            messageEditText.clearFocus();
            InputUtils.hideKeyboard(this);
            return;
        }

        super.onBackPressed();
    }

    private class UpdateLayoutRunnable implements Runnable {

        @Override
        public void run() {
            updateLayout();

            // Repeat this the same runnable code block again after UI_UPDATE_PERIOD
            updateHandler.postDelayed(updateRunnable, UI_UPDATE_PERIOD);
        }

    }
}
