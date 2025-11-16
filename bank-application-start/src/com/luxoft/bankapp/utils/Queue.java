package com.luxoft.bankapp.utils;

import java.util.ArrayList;
import java.util.List;

public class Queue<T> {

    private final List<T> items = new ArrayList<>();

    public synchronized void add(T item) {
        items.add(item);
        notifyAll();
    }

    public synchronized T poll() {
        while (items.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return items.removeFirst();
    }

    public synchronized T peek() {
        if (items.isEmpty()) {
            return null;
        }
        return items.getFirst();
    }

    public synchronized int size() {
        return items.size();
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }
}
