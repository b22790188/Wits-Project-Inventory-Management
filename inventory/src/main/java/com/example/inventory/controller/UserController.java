package com.example.inventory.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.example.inventory.dto.ApiResponse;
import com.example.inventory.dto.AuthResponseDto;
import com.example.inventory.dto.SigninRequestDto;
import com.example.inventory.dto.SignupRequestDto;
import com.example.inventory.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/1.0/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<ApiResponse<AuthResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        AuthResponseDto reponse = userService.signup(signupRequest);
        return ResponseEntity.ok(new ApiResponse<>(reponse));
    }

    @PostMapping(value = "/signin", consumes = "application/json")
    public ResponseEntity<ApiResponse<AuthResponseDto>> signin(@Valid @RequestBody SigninRequestDto signinRequest) {
        AuthResponseDto reponse = userService.signin(signinRequest);
        return ResponseEntity.ok(new ApiResponse<>(reponse));
    }
}
