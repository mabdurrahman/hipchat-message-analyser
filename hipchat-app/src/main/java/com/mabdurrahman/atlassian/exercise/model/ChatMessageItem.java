package com.mabdurrahman.atlassian.exercise.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/10/16.
 */
public class ChatMessageItem extends BaseObject implements Parcelable {

    private static final long serialVersionUID = 1556747903054882913L;

    private String rawMessage;

    private Spanned formattedMessage;

    private Date date;

    private boolean self;

    public ChatMessageItem() {
    }

    public ChatMessageItem(String rawMessage, Date date, boolean self) {
        this.rawMessage = rawMessage;
        this.date = date;
        this.self = self;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public Spanned getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(Spanned formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    protected ChatMessageItem(Parcel in) {
        rawMessage = in.readString();
        formattedMessage = (Spanned) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        self = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rawMessage);
        TextUtils.writeToParcel(formattedMessage, dest, flags);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeByte((byte) (self ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ChatMessageItem> CREATOR = new Parcelable.Creator<ChatMessageItem>() {
        @Override
        public ChatMessageItem createFromParcel(Parcel in) {
            return new ChatMessageItem(in);
        }

        @Override
        public ChatMessageItem[] newArray(int size) {
            return new ChatMessageItem[size];
        }
    };
}
