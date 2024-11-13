package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.events.EventComparator;

public class DeferredEventSubscriber<T extends Event<?>> implements Subscriber<T>, Iterable<T> {
    private final Queue<T> unprocessedEvents;

    public DeferredEventSubscriber() {
        this.unprocessedEvents = new PriorityQueue<>(new EventComparator<>());
    }

    /**
     * Store an event for processing at a later time.
     *
     * @param event the event to be processed
     * @throws IllegalArgumentException if the event is null
     */
    @Override
    public void onEvent(T event) {
        if (event == null) {
            throw new IllegalArgumentException("The given event is null");
        }

        unprocessedEvents.offer(event);
    }

    /**
     * Get an iterator for the unprocessed events. The iterator should provide the events sorted by
     * their priority in descending order. Events with equal priority are ordered in ascending order
     * of their timestamps.
     *
     * @return an iterator for the unprocessed events
     */
    @Override
    public Iterator<T> iterator() {
        return unprocessedEvents.iterator();
    }

    /**
     * Check if there are unprocessed events.
     *
     * @return true if there are unprocessed events, false otherwise
     */
    public boolean isEmpty() {
        return unprocessedEvents.isEmpty();
    }

}