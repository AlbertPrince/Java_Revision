package com.prince.techtest.userapi.Controller;

import com.prince.techtest.userapi.model.User;

import jakarta.validation.Valid;

import com.prince.techtest.userapi.Service.UserService;
import com.prince.techtest.userapi.dto.UserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<User>> getUsersPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {
            Sort sort = direction.equalsIgnoreCase("desc") ? 
                Sort.by(Sort.Direction.DESC, sortBy) : 
                Sort.by(Sort.Direction.ASC, sortBy);

            Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userService.getUsersPaginated(pageable);
        return ResponseEntity.ok(usersPage);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<User>> createUsers(@RequestBody @Valid List<UserDTO> userDTOs) {
        List<User> users = userDTOs.stream()
                .map(dto -> new User(dto.getName(), dto.getEmail()))
                .toList();
        return ResponseEntity.ok(userService.saveAllUsers(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String email) {
        List<User> users = userService.searchUsers(name, email);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO userDTO) {
        User user = new User(userDTO.getName(), userDTO.getEmail());
        User savedUser = userService.saveUser(user);

        URI location = URI.create(String.format("/api/users/%s", savedUser.getId()));
        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return userService.updateUser(id, userDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreUser(@PathVariable Long id){
        userService.restoreUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public List<User> getDeletedUsers() {
        return userService.getDeletedUsers();
    }
}
