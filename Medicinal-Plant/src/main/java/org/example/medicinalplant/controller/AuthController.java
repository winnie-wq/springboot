package org.example.medicinalplant.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.medicinalplant.common.R;
import org.example.medicinalplant.dto.LoginResponse;
import org.example.medicinalplant.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<LoginResponse> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");
        return authService.register(username, password, nickname);
    }

    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody Map<String, String> body) {
        return authService.login(body.get("username"), body.get("password"));
    }
}
