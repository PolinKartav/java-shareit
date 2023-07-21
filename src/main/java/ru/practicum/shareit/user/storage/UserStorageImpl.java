package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistedException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {
    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public Optional<User> createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new AlreadyExistedException("Email already exists");
        }
        user.setId(getNewId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return Optional.of(user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        User user = users.get(userId).toBuilder().build();
        return Optional.of(user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    @Override
    public User updateUser(User user) {
        if (emails.contains(user.getEmail()) && !users.get(user.getId()).getEmail().equals(user.getEmail())) {
            throw new AlreadyExistedException("Email already exists");
        }
        emails.remove(users.get(user.getId()).getEmail());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        emails.remove(getUserById(userId).get().getEmail());
        users.remove(userId);
    }

    private Long getNewId() {
        return id++;
    }
}
