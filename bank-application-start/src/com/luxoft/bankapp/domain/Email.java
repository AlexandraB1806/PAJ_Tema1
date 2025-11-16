package com.luxoft.bankapp.domain;

public class Email {
    private final String from;
    private final String to;

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public Email(String from, String to) {
        this.from = from;
        this.to = to;
    }
}
