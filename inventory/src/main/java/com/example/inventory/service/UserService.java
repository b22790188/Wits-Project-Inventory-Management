package com.example.inventory.service;

import com.example.inventory.dto.AuthResponseDto;
import com.example.inventory.dto.SigninRequestDto;
import com.example.inventory.dto.SignupRequestDto;

public interface UserService {
    
    AuthResponseDto signup(SignupRequestDto signupRequest);

    AuthResponseDto signin(SigninRequestDto signinRequest);
}
