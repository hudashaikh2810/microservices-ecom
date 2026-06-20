package com.Ecom.cart_service.cart_service.DTO;

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
        private Long guestId;

        // getters + setters + constructor

}
