package io.arrakis.controller;

import io.arrakis.auth.JWTUtil;
import io.arrakis.exception.InvalidJwtTokenException;
import io.arrakis.service.RetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DuneController {

    @Autowired
    RetryService retryService;


    @PostMapping("/create-token")
    public ResponseEntity<String> createToken(@RequestParam String username) {
        String token = JWTUtil.create(username);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/create-token-ttl")
    public ResponseEntity<String> createTokenWithTtl(@RequestParam String username, @RequestParam long ttl) {
        String token = JWTUtil.createWithExpiry(username, ttl);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/message")
    public ResponseEntity<String> printMessage() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = "Hello, " + user.getUsername() + "! Your token is valid.";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        throw new InvalidJwtTokenException("error");
    }

    @GetMapping("/retry")
    public String retry(@RequestParam String message) {
        return retryService.retry(message);
    }
}