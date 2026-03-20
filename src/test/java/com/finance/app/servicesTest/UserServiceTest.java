package com.finance.app.servicesTest;

import com.finance.app.dto.UserRequestDto;
import com.finance.app.dto.UserResponseDto;
import com.finance.app.model.User;
import com.finance.app.repository.UserRepository;
import com.finance.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveAndReturnResponse() {
        UserRequestDto request = new UserRequestDto("test@example.com", "password123", "Test", "User");
        User saved = User.builder()
                .id(1L)
                .email(request.getEmail())
                .passwordHash(request.getPasswordHash())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

        UserResponseDto response = userService.createUser(request);

        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = User.builder().id(1L).email("u@mail.com").firstName("A").lastName("B").passwordHash("x").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(1L);

        assertEquals(1L, response.getId());
        assertEquals("u@mail.com", response.getEmail());
    }

    @Test
    void getUserById_shouldThrowWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(99L));

        assertEquals("User not found with id: 99", ex.getMessage());
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        User user = User.builder().id(2L).email("mail@test.com").firstName("M").lastName("N").passwordHash("x").build();
        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserByEmail("mail@test.com");

        assertEquals(2L, response.getId());
        assertEquals("mail@test.com", response.getEmail());
    }

    @Test
    void getAllUsers_shouldMapAllUsers() {
        User u1 = User.builder().id(1L).email("a@mail.com").firstName("A").lastName("A").passwordHash("x").build();
        User u2 = User.builder().id(2L).email("b@mail.com").firstName("B").lastName("B").passwordHash("y").build();
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponseDto> responses = userService.getAllUsers();

        assertEquals(2, responses.size());
        assertEquals("a@mail.com", responses.get(0).getEmail());
        assertEquals("b@mail.com", responses.get(1).getEmail());
    }

    @Test
    void updateUser_shouldUpdateAndReturnResponse() {
        User existing = User.builder().id(1L).email("old@mail.com").firstName("Old").lastName("Name").passwordHash("old").build();
        UserRequestDto request = new UserRequestDto("new@mail.com", "newPass123", "New", "User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UserResponseDto response = userService.updateUser(1L, request);

        assertEquals("new@mail.com", response.getEmail());
        assertEquals("New", response.getFirstName());
        assertEquals("User", response.getLastName());
    }

    @Test
    void deleteUser_shouldDeleteWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowWhenNotExists() {
        when(userRepository.existsById(5L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(5L));

        assertEquals("User not found with id: 5", ex.getMessage());
    }
}

