package com.prince.techtest.userapi.Service;

import com.prince.techtest.userapi.model.User;
import com.prince.techtest.userapi.repository.UserRepository;
import com.prince.techtest.userapi.dto.UserDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {

        // Given
        List<User> mockUsers = Arrays.asList(
            new User("Alice", "alice@example.com"),
            new User("Bob", "bob@example.com")
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        // When
        List<User> users = userService.getAllUsers();

        // Then
        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void testSaveUser() {

        // Given
        User user1 = new User("Alice", "alice@example.com");
        when(userRepository.save(user1)).thenReturn(user1);

        // When
        User savedUser = userService.saveUser(user1);

        // Then
        assertNotNull(savedUser);
        assertEquals("Alice", savedUser.getName());
        assertEquals("alice@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void testGetUserById_whenFound() {

        // Given
        User user1 = new User("Alice", "alice@example.com");
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));

        // When
        Optional<User> foundUser = userService.getUserById(1L);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("Alice", foundUser.get().getName());
        assertEquals("alice@example.com", foundUser.get().getEmail());
    
    }

    @Test
    void testGetUserById_whenNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<User> foundUser = userService.getUserById(1L);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteUserById_whenExists() {
        // Given
        User user1 = new User("Alice", "alice@example.com");
        user1.setId(1L);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));

        // When
        userService.deleteUserById(1L);

        // Then
        assertTrue(user1.isDeleted());
        // verify(userRepository, times(1)).findByIdAndDeletedFalse(1L);
        // verify(userRepository, times(1)).save(user1);
        }
        
    @Test
    void testDeleteUserById_whenNotExists() {
        // Given
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        // When
        userService.deleteUserById(1L);

        // Then
        verify(userRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_whenExists() {
        // Given
        User user1 = new User("Alice", "alice@example.com");
        user1.setId(1L);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user1));


        UserDTO userDto = new UserDTO("Alice Updated", "alice.updated@example.com");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // When
        Optional<User> updatedUser = userService.updateUser(1L, userDto);

        // Then
        assertTrue(updatedUser.isPresent());
        assertEquals("Alice Updated", updatedUser.get().getName());
        assertEquals("alice.updated@example.com", updatedUser.get().getEmail());
        verify(userRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(userRepository, times(1)).save(user1);
    }

    
}