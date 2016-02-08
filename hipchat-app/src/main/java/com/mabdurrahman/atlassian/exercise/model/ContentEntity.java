package com.mabdurrahman.atlassian.exercise.model;

import java.util.regex.Matcher;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class ContentEntity {
    public enum Type {
        MENTION, EMOTICON, URL
    }
    protected int start;
    protected int end;
    protected final String value;

    protected final Type type;

    public ContentEntity(int start, int end, String value, Type type) {
        this.start = start;
        this.end = end;
        this.value = value;
        this.type = type;
    }

    public ContentEntity(Matcher matcher, Type type, int groupNumber) {
        // Offset -1 on start index to include first-char-symbols for entities
        this(matcher, type, groupNumber, -1);
    }

    public ContentEntity(Matcher matcher, Type type, int groupNumber, int startOffset) {
        this(matcher.start(groupNumber) + startOffset, matcher.end(groupNumber), matcher.group(groupNumber), type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ContentEntity)) {
            return false;
        }

        ContentEntity other = (ContentEntity)obj;

        if (this.type.equals(other.type) &&
                this.start == other.start &&
                this.end == other.end &&
                this.value.equals(other.value)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() + this.value.hashCode() + this.start + this.end;
    }

    @Override
    public String toString() {
        return value + "(" + type +") [" +start + "," + end +"]";
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
}
