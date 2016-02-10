package com.mabdurrahman.atlassian.exercise.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mabdurrahman.atlassian.exercise.R;
import com.mabdurrahman.atlassian.exercise.model.ChatMessageItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/10/16.
 */
public class MessagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_SELF = 100;
    private final int VIEW_TYPE_REPLY = 200;

    private Context context;
    private OnItemClickListener onItemClickListener;

    private List<ChatMessageItem> itemsList = new ArrayList<>();

    public MessagesRecyclerAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<ChatMessageItem> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<ChatMessageItem> items) {
        if (items == null)
            return;

        this.itemsList = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position).isSelf()? VIEW_TYPE_SELF : VIEW_TYPE_REPLY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutRes = viewType == VIEW_TYPE_SELF? R.layout.list_item_chat_message_right : R.layout.list_item_chat_message_left;
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(layoutRes, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.update(position, itemsList.get(position));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.container_content)
        LinearLayout contentContainer;

        @Bind(R.id.container_message)
        LinearLayout messageContainer;

        @Bind(R.id.label_date)
        TextView date;

        @Bind(R.id.label_message)
        TextView message;

        int position;
        ChatMessageItem item;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void update(int position, ChatMessageItem item) {
            this.position = position;
            this.item = item;

            message.setText(item.getFormattedMessage() != null? item.getFormattedMessage() : item.getRawMessage());
            date.setText(DateUtils.getRelativeTimeSpanString(item.getDate().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
        }

        @Override
        public void onClick(View v) {
            if (item == null || onItemClickListener == null) {
                return;
            }

            onItemClickListener.onItemClick(position, item);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, ChatMessageItem item);
    }

}
