package com.luxoft.bankapp.service;

import com.luxoft.bankapp.domain.Email;
import com.luxoft.bankapp.utils.Queue;

import java.util.LinkedList;

public class EmailService {
    private final Queue<Email> queue;

    private final Thread worker;
    private volatile boolean closed = false;

    public EmailService() {
        this.queue = new Queue<>();

        this.worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Email email = queue.poll();
                if (email == null) {
                    break;
                }

                emulateSend(email);
            }
        }, "EmailService-Worker");

        this.worker.start();
    }

    public void sendNotificationEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null");
        }

        if (closed) {
            throw new IllegalStateException("EmailService is closed");
        }

        queue.add(email);
    }

    private void emulateSend(Email email) {
        System.out.println("Sending email to " + email.getTo());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        closed = true;
        worker.interrupt();
        try {
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
