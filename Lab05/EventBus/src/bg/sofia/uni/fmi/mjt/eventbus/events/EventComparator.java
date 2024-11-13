package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.util.Comparator;

public class EventComparator<T extends Event<?>> implements Comparator<T> {
    @Override
    public int compare(T event1, T event2) {
        int priority = Integer.compare(event1.getPriority(), event2.getPriority());

        if (priority != 0) {
            return priority;
        }

        return event1.getTimestamp().compareTo(event2.getTimestamp());
    }
}
