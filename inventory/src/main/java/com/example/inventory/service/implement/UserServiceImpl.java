package com.example.inventory.service.implement;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventory.dto.UserDto;
import com.example.inventory.dto.auth.AuthResponseDto;
import com.example.inventory.dto.auth.SigninRequestDto;
import com.example.inventory.dto.auth.SignupRequestDto;
import com.example.inventory.entity.User;
import com.example.inventory.exception.auth.EmailAlreadyExistsException;
import com.example.inventory.exception.auth.SigninFailException;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.JwtUtil;
import com.example.inventory.service.UserService;

@Service
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    @Transactional
    public AuthResponseDto signup(SignupRequestDto signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(encodedPassword);

        userRepository.save(newUser);

        UserDto userDto = new UserDto(newUser.getUserId(), newUser.getName(), newUser.getEmail());
        String token = jwtUtil.generateToken(userDto);

        return new AuthResponseDto(token, expiration, userDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto signin(SigninRequestDto signinRequest) {

        Optional<User> optionalUser = userRepository.findByEmail(signinRequest.getEmail());

        if (optionalUser.isEmpty()) {
            throw new SigninFailException("Invalid email");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new SigninFailException("Invalid password");
        }

        UserDto userDto = new UserDto(user.getUserId(), user.getName(), user.getEmail());
        String token = jwtUtil.generateToken(userDto);

        return new AuthResponseDto(token, expiration, userDto);
    }
} 
