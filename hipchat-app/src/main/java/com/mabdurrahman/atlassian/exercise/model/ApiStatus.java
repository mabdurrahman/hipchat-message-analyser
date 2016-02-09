package com.mabdurrahman.atlassian.exercise.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/9/16.
 */
public enum ApiStatus {
    OK("OK"), ERROR("ERROR");

    private final String value;

    ApiStatus(String v) {
        value = v;
    }

    @JsonValue
    public String toString() {
        return value;
    }

    @JsonCreator
    public static ApiStatus fromString(String status) {
        for (ApiStatus s : ApiStatus.values()) {
            if (s.value.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}
