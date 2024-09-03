package com.example.inventory.service;

import com.example.inventory.dto.auth.AuthResponseDto;
import com.example.inventory.dto.auth.SigninRequestDto;
import com.example.inventory.dto.auth.SignupRequestDto;

public interface UserService {
    
    AuthResponseDto signup(SignupRequestDto signupRequest);

    AuthResponseDto signin(SigninRequestDto signinRequest);
}
