package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultUserProfile implements UserProfile, Comparable<UserProfile> {
    private final String username;
    private final Set<Interest> interests;
    private final Collection<UserProfile> friends;

    public DefaultUserProfile(String username) {
        this.username = username;
        interests = EnumSet.noneOf(Interest.class);
        friends = new HashSet<>();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<Interest> getInterests() {
        return Collections.unmodifiableSet(interests);
    }

    public boolean addInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("The interest you are trying to add is null");
        }

        return interests.add(interest);
    }

    @Override
    public boolean removeInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("The interest you are trying to remove is null");
        }

        return interests.remove(interest);
    }

    @Override
    public Collection<UserProfile> getFriends() {
        return Collections.unmodifiableCollection(friends);
    }

    @Override
    public boolean addFriend(UserProfile userProfile) {
        if (userProfile == null || userProfile == this) {
            throw new IllegalArgumentException("The user you are trying to unfriend is null");
        }

        if (!isFriend(userProfile)) {
            friends.add(userProfile);
            userProfile.addFriend(this);
            return true;
        }

        return false;
    }

    @Override
    public boolean unfriend(UserProfile userProfile) {
        if (userProfile == this) {
            throw new IllegalArgumentException("The user you are trying to unfriend is null");
        }

        if (isFriend(userProfile)) {
            friends.remove(userProfile);
            userProfile.unfriend(this);
            return true;
        }

        return false;
    }

    @Override
    public boolean isFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("This user is null");
        }

        return friends.contains(userProfile);
    }

    @Override
    public int compareTo(UserProfile other) {
        return this.getUsername().compareTo(other.getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUserProfile that = (DefaultUserProfile) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
