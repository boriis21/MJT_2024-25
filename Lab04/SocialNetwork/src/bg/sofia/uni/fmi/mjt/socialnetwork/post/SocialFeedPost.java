package bg.sofia.uni.fmi.mjt.socialnetwork.post;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SocialFeedPost implements Comparable<SocialFeedPost>, Post {
    private final String uniqueId;
    private final UserProfile author;
    private final String content;
    private final LocalDateTime publishedAt;
    private final Map<ReactionType, Set<UserProfile>> reactions;

    public SocialFeedPost(UserProfile author, String content) {
        if (author == null || content == null) {
            throw new IllegalArgumentException("One of the passed arguments for creating post is null");
        }

        if (content.isEmpty()) {
            throw new IllegalArgumentException("The content of the post cannot be empty");
        }

        this.author = author;
        this.content = content;
        this.publishedAt = LocalDateTime.now();
        this.uniqueId = UUID.randomUUID().toString();
        this.reactions = new EnumMap<>(ReactionType.class);
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public UserProfile getAuthor() {
        return author;
    }

    @Override
    public LocalDateTime getPublishedOn() {
        return publishedAt;
    }

    @Override
    public String getContent() {
        return content;
    }

    private ReactionType findUserReaction(UserProfile user) {
        if (user == null) {
            throw new IllegalArgumentException("The user trying to remove a reaction is null");
        }

        for (Map.Entry<ReactionType, Set<UserProfile>> entry : reactions.entrySet()) {
            if (entry.getValue().contains(user)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public boolean addReaction(UserProfile userProfile, ReactionType reactionType) {
        if (reactionType == null) {
            throw new IllegalArgumentException("The reaction you are trying to add is null");
        }

        ReactionType userCurrentReaction = findUserReaction(userProfile);

        if (userCurrentReaction != null) {
            removeReaction(userProfile);
        }

        if (!reactions.containsKey(reactionType)) {
            reactions.put(reactionType, new HashSet<>());
        }

        reactions.get(reactionType).add(userProfile);

        return userCurrentReaction == null;
    }

    @Override
    public boolean removeReaction(UserProfile userProfile) {
        ReactionType userReaction = findUserReaction(userProfile);

        if (userReaction == null) {
            return false;
        }

        boolean removed = reactions.get(userReaction).remove(userProfile);

        if (reactions.get(userReaction).isEmpty()) {
            reactions.remove(userReaction);
        }

        return removed;
    }

    @Override
    public Map<ReactionType, Set<UserProfile>> getAllReactions() {
        return Collections.unmodifiableMap(reactions);
    }

    @Override
    public int getReactionCount(ReactionType reactionType) {
        if (reactionType == null) {
            throw new IllegalArgumentException("The reaction type you are trying to get the count of is null");
        }

        if (!reactions.containsKey(reactionType)) {
            return 0;
        }

        return reactions.get(reactionType).size();
    }

    @Override
    public int totalReactionsCount() {
        ReactionType[] types = ReactionType.values();
        int totalCount = 0;

        for (ReactionType type : types) {
            totalCount += getReactionCount(type);
        }

        return totalCount;
    }

    @Override
    public int compareTo(SocialFeedPost other) {
        return this.uniqueId.compareTo(other.uniqueId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialFeedPost that = (SocialFeedPost) o;
        return Objects.equals(uniqueId, that.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uniqueId);
    }
}
