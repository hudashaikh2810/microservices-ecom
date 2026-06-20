package com.Ecom.user_service;

import com.Ecom.user_service.Dto.UserDto;
import com.Ecom.user_service.Entity.User;
import com.Ecom.user_service.Exception.UserWithIdNotFound;
import com.Ecom.user_service.Repository.UserRepository;
import com.Ecom.user_service.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setAuthUserId(1L);
        userDto.setEmail("abc");
        userDto.setName("ABC");
        userDto.setPhone("12345678");
        userDto.setAddresses(new ArrayList<>());
        User createdUser = new User();
        createdUser.setId(2L);
        createdUser.setAuthUserId(1L);
        createdUser.setEmail("abc");
        createdUser.setName("ABC");
        createdUser.setPhone("12345678");
        createdUser.setAddresses(new ArrayList<>());
        when(userRepository.save(any(User.class))).thenReturn(createdUser);
        UserDto createdUserDto = userService.addUser(userDto);
        assertNotNull("Object is null", createdUserDto);
        assertEquals(createdUserDto.getAuthUserId(), userDto.getAuthUserId());
    }

    @Test
    public void testGetUserByAuthServiceId() {
        User createdUser = new User();
        createdUser.setId(2L);
        createdUser.setAuthUserId(1L);
        createdUser.setEmail("abc");
        createdUser.setName("ABC");
        createdUser.setPhone("12345678");
        when(userRepository.findByAuthUserId(1L)).thenReturn(Optional.of(createdUser));
        UserDto getUserByAuthId1 = userService.getUserByAuthServiceId(1L);
        assertNotNull("Value is null", getUserByAuthId1);
        assertEquals(1L, getUserByAuthId1.getAuthUserId());
        assertThrows(UserWithIdNotFound.class, () -> userService.getUserByAuthServiceId(2L));
    }

    @Test
    public void testDeleteUser() {
        User createdUser = new User();
        createdUser.setId(2L);
        createdUser.setAuthUserId(1L);
        createdUser.setEmail("abc");
        createdUser.setName("ABC");
        createdUser.setPhone("12345678");
        when(userRepository.existsById(1L)).thenReturn(true); // ✅ stub the method used in service

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
        when(userRepository.existsById(2L)).thenReturn(false); // ✅ stub the method used in service
        userService.deleteUser(2L);

        verify(userRepository, Mockito.never()).deleteById(2L);


    }

    @Test
    public void updated() {

        UserDto userDto = new UserDto();
        userDto.setAuthUserId(1L);
        userDto.setEmail("abcd");
        userDto.setName("ABC");
        userDto.setPhone("123456789");
        userDto.setAddresses(new ArrayList<>());
        User createdUser = new User();
        createdUser.setId(2L);
        createdUser.setAuthUserId(1L);
        createdUser.setEmail("abc");
        createdUser.setName("ABC");
        createdUser.setPhone("12345678");
        createdUser.setAddresses(new ArrayList<>());
        when(userRepository.findByAuthUserId(1L)).thenReturn(Optional.of(createdUser));
        UserDto updatedUser = userService.updateUserByAuthUserId(1L, userDto);
        assertEquals(userDto.getPhone(), updatedUser.getPhone());
        assertThrows(RuntimeException.class, () -> userService.updateUserByAuthUserId(2L, userDto));
    }


}
