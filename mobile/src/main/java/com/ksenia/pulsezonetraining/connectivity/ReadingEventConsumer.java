package com.ksenia.pulsezonetraining.connectivity;

/**
 * Created by ksenia on 19.02.19.
 */

public interface ReadingEventConsumer<T> {
    void accept(T namesOfDevices);
}
