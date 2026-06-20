package com.Ecom.user_service;


import com.Ecom.user_service.Dto.AddressDto;
import com.Ecom.user_service.Entity.Address;
import com.Ecom.user_service.Entity.User;
import com.Ecom.user_service.Repository.AddressRepository;
import com.Ecom.user_service.Repository.UserRepository;
import com.Ecom.user_service.Exception.AddressWithIdNotFound;
import com.Ecom.user_service.Service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private AddressDto addressDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        address = Address.builder()
                .id(10L)
                .city("City")
                .state("State")
                .country("Country")
                .street("Street")
                .postalCode("12345")
                .tag("Home")
                .status("Active")
                .user(user)
                .build();

        addressDto = AddressDto.builder()
                .city("City")
                .state("State")
                .country("Country")
                .street("Street")
                .postalCode("12345")
                .tag("Home")
                .status("Active")
                .build();
    }

    @Test
    void addAddress_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.addAddress(1L, addressDto);

        assertNotNull(result);
        assertEquals("City", result.getCity());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void addAddress_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> addressService.addAddress(1L, addressDto));
        assertEquals("Cannot saved address as user doesn't exisit", ex.getMessage());
    }

    @Test
    void getAddress_Success() {
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        AddressDto result = addressService.getAddress(10L);

        assertNotNull(result);
        assertEquals("City", result.getCity());
    }

    @Test
    void getAddress_NotFound_ThrowsException() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(AddressWithIdNotFound.class, () -> addressService.getAddress(10L));
    }

    @Test
    void getAllUserAddress_ReturnsAddresses() {
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(address)));

        List<AddressDto> result = addressService.getAllUserAddress(1L);

        assertEquals(1, result.size());
        assertEquals("City", result.get(0).getCity());
    }

    @Test
    void getAllUserAddress_NoAddresses_ReturnsEmptyList() {
        when(addressRepository.findByUserId(1L)).thenReturn(Optional.empty());

        List<AddressDto> result = addressService.getAllUserAddress(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAddress_Success() {
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        addressService.delete(10L);

        verify(userRepository, times(1)).save(user);
        assertFalse(user.getAddresses().contains(address));
    }

    @Test
    void deleteAddress_NotFound_ThrowsException() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> addressService.delete(10L));
    }

    @Test
    void updateAddress_Success() {
        AddressDto updatedDto = AddressDto.builder()
                .city("NewCity")
                .state("NewState")
                .country("NewCountry")
                .street("NewStreet")
                .postalCode("54321")
                .tag("Work")
                .status("Inactive")
                .build();

        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        AddressDto result = addressService.update(10L, updatedDto);

        assertEquals("NewCity", result.getCity());
        assertEquals("Work", result.getTag());
    }

    @Test
    void updateAddress_NotFound_ThrowsException() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(AddressWithIdNotFound.class, () -> addressService.update(10L, addressDto));
    }
}

