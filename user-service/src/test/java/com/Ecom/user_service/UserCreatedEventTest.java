package com.Ecom.user_service;

import com.Ecom.user_service.Dto.UserCreatedEvent;
import com.Ecom.user_service.Dto.UserDto;
import com.Ecom.user_service.Entity.User;
import com.Ecom.user_service.Repository.UserRepository;
import com.Ecom.user_service.Service.UserCreatedConsumer;
import com.Ecom.user_service.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserCreatedEventTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserCreatedConsumer consumer;
    @Test
    public void testToCheckIfUserIsNotPresent()
    {
        UserCreatedEvent event=new UserCreatedEvent(1L,"huda@gmail.com");
        Acknowledgment ack = mock(Acknowledgment.class);
        when(userRepository.findByAuthUserId(1L)).thenReturn(Optional.empty());
        consumer.createUser(event,ack);
        ArgumentCaptor<UserDto> captor = ArgumentCaptor.forClass(UserDto.class);
        verify(userService, times(1)).addUser(captor.capture());
        UserDto userDto = captor.getValue();

        assertThat(userDto.getAuthUserId()).isEqualTo(1L);
        assertThat(userDto.getEmail()).isEqualTo("huda@gmail.com");

        // Verify acknowledgment
        verify(ack, times(1)).acknowledge();

    }
    @Test
    void testCreateUser_ExistingUser_ShouldNotCallServiceButAcknowledge() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent();
        event.setUserId(1L);
        event.setEmail("huda@example.com");

        Acknowledgment ack = mock(Acknowledgment.class);

        when(userRepository.findByAuthUserId(1L)).thenReturn(Optional.of(new User()));

        // Act
        consumer.createUser(event, ack);

        // Assert
        verify(userService, never()).addUser(any());
        verify(ack, times(1)).acknowledge();
    }

}
