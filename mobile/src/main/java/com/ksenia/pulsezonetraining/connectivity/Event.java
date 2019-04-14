package com.ksenia.pulsezonetraining.connectivity;

/**
 * Created by ksenia on 26.02.19.
 */

public class Event<T> {

    public static enum Type {
        STATUS_CHANGED_CONNTECTED,
        STATUS_CHANGED_DISCONNTECTED,
        READING
    }

    private Type eventType;
    private T message;

    public Event(Type type, T message) {
        this.eventType = type;
        this.message = message;
    }

    public Event(Type type) {
        this.eventType = type;
    }

    public Type getEventType() {
        return eventType;
    }

    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }


}
