package com.finance.app.service;

import com.finance.app.dto.UserRequestDto;
import com.finance.app.dto.UserResponseDto;
import com.finance.app.model.User;
import com.finance.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(UserRequestDto requestDto) {
        User user = User.builder()
                .email(requestDto.getEmail())
                .passwordHash(requestDto.getPasswordHash())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return mapToResponseDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return mapToResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmail(requestDto.getEmail());
        user.setPasswordHash(requestDto.getPasswordHash());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());

        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDto mapToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
