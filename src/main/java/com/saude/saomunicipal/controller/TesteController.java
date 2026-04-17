package com.saude.saomunicipal.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/teste")
public class TesteController {

    @PostMapping
    public Map<String, Object> testar(@RequestBody Map<String, Object> body) {
        return body;
    }
}