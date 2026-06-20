package com.example.auth_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {

        private Long userId;
        private String email;

        // getters + setters + constructor

}
