package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.events.EventByTimestampComparator;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class EventBusImpl implements EventBus {
    private final Map<Class<? extends Event<?>>, Set<Subscriber<?>>> subscriberLog;
    private final SortedSet<Event<?>> events;

    public EventBusImpl() {
        this.subscriberLog = new HashMap<>();
        this.events = new TreeSet<>(new EventByTimestampComparator<>());
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("The event type, subscriber or both are null");
        }

        subscriberLog.putIfAbsent(eventType, new HashSet<>());
        subscriberLog.get(eventType).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
        throws MissingSubscriptionException {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("The event type, subscriber or both are null");
        }

        Set<Subscriber<?>> subscribers = subscriberLog.get(eventType);

        if (subscribers == null || !subscribers.remove(subscriber)) {
            throw new MissingSubscriptionException("The subscriber is not subscribed to the event type");
        }
    }

    private <T extends Event<?>> void notifySubscriber(Subscriber<?> subscriber, T event) {
        @SuppressWarnings("unchecked")
        Subscriber<? super T> typedSubscriber = (Subscriber<? super T>) subscriber;
        typedSubscriber.onEvent(event);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("The event is null");
        }

        @SuppressWarnings("unchecked")
        Class<? extends Event<?>> eventType = (Class<? extends Event<?>>) event.getClass();
        subscriberLog.putIfAbsent(eventType, new HashSet<>());
        Set<Subscriber<?>> subscribers = subscriberLog.get(eventType);

        if (subscribers != null) {
            for (Subscriber<?> subscriber : subscribers) {
                notifySubscriber(subscriber, event);
            }
        }

        events.add(event);
    }

    @Override
    public void clear() {
        subscriberLog.clear();
        events.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {
        if (eventType == null || from == null || to == null) {
            throw new IllegalArgumentException("The event type or the start or the end time or all of them are null");
        }

        if (from.equals(to) || from.isAfter(to)) {
            return Collections.emptySet();
        }

        Set<Event<?>> filteredEvents = new TreeSet<>(new EventByTimestampComparator<>());
        for (Event<?> event : events) {
            if (eventType == event.getClass()) {
                Instant currentTime = event.getTimestamp();
                if (!currentTime.isBefore(from) && currentTime.isBefore(to)) {
                    filteredEvents.add(event);
                }
            }
        }

        return Collections.unmodifiableCollection(filteredEvents);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("The event type is null");
        }

        Set<Subscriber<?>> eventSubscribers = subscriberLog.get(eventType);
        return eventSubscribers == null ? Collections.emptySet() : Collections.unmodifiableSet(eventSubscribers);
    }
}