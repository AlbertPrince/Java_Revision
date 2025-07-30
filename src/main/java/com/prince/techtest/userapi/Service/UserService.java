package com.prince.techtest.userapi.Service;

import com.prince.techtest.userapi.dto.UserDTO;
import com.prince.techtest.userapi.model.User;
import com.prince.techtest.userapi.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // public List<User> getAllUsers() {
    //     return repo.findAll();
    // }

    // public Optional<User> getUserById(Long id) {
    //     return repo.findById(id);
    // }

    // public List<User> getAllUsers() {
    //     return repo.findByDeletedFalse(); // Fetch only non-deleted users
    // }

    public List<User> saveAllUsers(List<User> users) {
        return repo.saveAll(users); 
    }

    public Page<User> getUsersPaginated(Pageable pageable) {
        return repo.findByDeletedFalse(pageable); // Fetch non-deleted users with pagination
    }

    public Optional<User> getUserById(Long id) {
        return repo.findByIdAndDeletedFalse(id); // Fetch non-deleted user by ID
    }

    public List<User> searchUsers(String name, String email){
        if(name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
            return repo.findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(name, email);
        } else if(name != null && !name.isEmpty()) {
            return repo.findByNameContainingIgnoreCase(name);
        } else if(email != null && !email.isEmpty()) {
            return repo.findByEmailContainingIgnoreCase(email);
        } else {
            return repo.findAll(); // Return all users if no search criteria
        }
    }

    public List<User> getDeletedUsers() {
        return repo.findByDeletedTrue(); // Fetch only deleted users
    }

    public User saveUser(User user) {
        return repo.save(user);
    }

    public Optional<User> updateUser(Long id, UserDTO userDto){
        return repo.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userDto.getName());
                    existingUser.setEmail(userDto.getEmail());
                    return repo.save(existingUser);
                });
    }

    // public void deleteUserById(Long id) {
    //     if(!repo.existsById(id)) {
    //         throw new IllegalArgumentException("User with id " + id + " does not exist.");
    //     }
    //     repo.deleteById(id);
    // }

    public void deleteUserById(Long id) {
        repo.findById(id).ifPresent(user -> {
            user.setDeleted(true); // Mark as deleted instead of removing
            repo.save(user);
        });
    }

    public void restoreUserById(Long id) {
        repo.findById(id).ifPresent(user -> {
            user.setDeleted(false); // Restore the user
            repo.save(user);
        });
    }
}
