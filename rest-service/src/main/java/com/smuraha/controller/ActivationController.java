package com.smuraha.controller;

import com.smuraha.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class ActivationController {
    private final UserActivationService userActivationService;

    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id){
        boolean res = userActivationService.activate(id);
        if(res){
            return ResponseEntity.ok().body("Регистрация успешно завершена");
        }
        return ResponseEntity.internalServerError().build();
    }
}
