package com.moreapp.starterkit.hook;

public class HookResponse {
    public enum Status {
        SUCCESS,
        INVALID
    }

    private Status status;
    private String message;

    public HookResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
