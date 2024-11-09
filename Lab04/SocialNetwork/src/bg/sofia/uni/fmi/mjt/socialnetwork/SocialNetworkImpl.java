package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UsersByFriendsCountDescComparator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocialNetworkImpl implements SocialNetwork {
    private final Set<UserProfile> users;
    private final Collection<Post> posts;

    public SocialNetworkImpl() {
        this.users = new HashSet<>();
        this.posts = new ArrayList<>();
    }

    @Override
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("The user you want to register is null");
        }

        if (users.contains(userProfile)) {
            throw new UserRegistrationException("The user is already registered");
        }

        users.add(userProfile);
    }

    @Override
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("The user who is trying to create a post is null");
        }

        if (content == null) {
            throw new IllegalArgumentException("The content for the post you are trying to create is null");
        }

        if (!users.contains(userProfile)) {
            throw new UserRegistrationException("The user who is trying to create a post is not registered");
        }

        Post post = new SocialFeedPost(userProfile, content);
        posts.add(post);
        return post;
    }

    @Override
    public Collection<Post> getPosts() {
        return Collections.unmodifiableCollection(posts);
    }

    private boolean hasCommonInterests(UserProfile user1, UserProfile user2) {
        Set<Interest> interestsIntersection = new HashSet<>(user1.getInterests());
        interestsIntersection.retainAll(user2.getInterests());
        return !(interestsIntersection.isEmpty());
    }

    @Override
    public Set<UserProfile> getReachedUsers(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("The post for the feed is null");
        }

        UserProfile author = post.getAuthor();
        Set<UserProfile> reachedUsers = new HashSet<>();
        Queue<UserProfile> queue = new ArrayDeque<>();
        Set<UserProfile> visited = new HashSet<>();

        queue.add(author);
        visited.add(author);

        while (!queue.isEmpty()) {
            UserProfile currentUser = queue.poll();

            for (UserProfile friend : currentUser.getFriends()) {
                if (!visited.contains(friend)) {
                    visited.add(friend);
                    queue.add(friend);

                    if (friend != author && hasCommonInterests(author, friend)) {
                        reachedUsers.add(friend);
                    }
                }
            }
        }

        return Collections.unmodifiableSet(reachedUsers);
    }

    @Override
    public Set<UserProfile> getMutualFriends(UserProfile userProfile1, UserProfile userProfile2)
        throws UserRegistrationException {
        if (userProfile1 == null || userProfile2 == null) {
            throw new IllegalArgumentException("One of the users whose mutual friends you want is null");
        }

        if (!(users.contains(userProfile1) && (users.contains(userProfile2)))) {
            throw new UserRegistrationException("One of the users whose mutual friends you want is not registered");
        }

        Set<UserProfile> intersection = new HashSet<>(userProfile1.getFriends());
        intersection.retainAll(userProfile2.getFriends());

        return intersection;
    }

    @Override
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        SortedSet<UserProfile> sortedUsers = new TreeSet<>(new UsersByFriendsCountDescComparator());
        sortedUsers.addAll(getAllUsers());
        return sortedUsers;
    }
}
