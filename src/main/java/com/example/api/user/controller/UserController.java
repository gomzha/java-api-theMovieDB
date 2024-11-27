package com.example.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/")
    @Operation(summary = "Home endpoint", description = "Returns a greeting indicating Spring is here")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public String home() {
        return "Spring is here!";
    }
}
