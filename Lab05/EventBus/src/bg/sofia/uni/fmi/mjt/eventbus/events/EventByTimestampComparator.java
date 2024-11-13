package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.util.Comparator;

public class EventByTimestampComparator<T extends Event<?>> implements Comparator<T> {
    @Override
    public int compare(T event1, T event2) {
        return event1.getTimestamp().compareTo(event2.getTimestamp());
    }
}
