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
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mabdurrahman.atlassian.exercise.R;
import com.mabdurrahman.atlassian.exercise.adapter.MessagesRecyclerAdapter;
import com.mabdurrahman.atlassian.exercise.manager.ChatManager;
import com.mabdurrahman.atlassian.exercise.model.ChatMessageItem;
import com.mabdurrahman.atlassian.exercise.model.ContentEntity;
import com.mabdurrahman.atlassian.exercise.model.MessageItem;
import com.mabdurrahman.atlassian.exercise.utils.InputUtils;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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

        //invalidate cache on fresh start
        ChatManager.getInstance().invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the initial UI runnable
        updateHandler.post(updateRunnable);

        // Subscribe to the Pending Request if any
        if (ChatManager.getInstance().getMessageAnalyserRequestCache() != null) {
            ChatManager.getInstance().getMessageAnalyserRequestCache()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<MessageItem>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new AnalyseMessageSubscriber());
        }
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

        // Don't process two simultaneous Messages at a time
        if (ChatManager.getInstance().getMessageAnalyserRequestCache() != null) {
            Toast.makeText(this, R.string.msg_cant_process_messages_simultaneously, Toast.LENGTH_LONG).show();
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

       ChatManager.getInstance().analyseChatMessage(messageText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<MessageItem>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new AnalyseMessageSubscriber());
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

    private class AnalyseMessageSubscriber extends Subscriber<MessageItem> {
        @Override
        public final void onNext(MessageItem messageItem) {
            addReplyRawMessage(messageItem);

            ChatManager.getInstance().invalidate();
        }

        @Override
        public final void onError(Throwable throwable) {
            addReplyRawMessage(null);

            Log.e(TAG, throwable.getMessage(), throwable);
        }

        @Override
        public final void onCompleted() {
        }

    }
}
