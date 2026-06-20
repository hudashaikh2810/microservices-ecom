package com.Ecom.user_service.Service;

import com.Ecom.user_service.Dto.AddressDto;
import com.Ecom.user_service.Dto.UserDto;
import com.Ecom.user_service.Entity.Address;
import com.Ecom.user_service.Entity.User;
import com.Ecom.user_service.Exception.UserWithIdNotFound;
import com.Ecom.user_service.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final Logger logger= LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        logger.info("Converting user dto to entity email={}",userDto.getEmail());
        User u = convertToUserEntity(userDto);
        logger.info("Going to save user {} {} into db ",u.getEmail(),u.getAuthUserId());
        User savedUser=userRepository.save(u);
        return convertToUserDto(savedUser);
    }

    public void deleteUser(Long userId) {
        logger.info("Checking whether user with this id{} exisit or not",userId);
        if (userRepository.existsById(userId)) {
            logger.info("User woth id{} exists deleting the user",userId);
            userRepository.deleteById(userId);
            logger.info("User with id deleted");
        }
    }

    public UserDto updateUserByAuthUserId(Long authUserId,UserDto dto)
    {
        logger.info("Going to check User with authUserId={}",authUserId);
        User updatedUser= userRepository.findByAuthUserId(authUserId).map(user->{
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            if(dto.getAddresses()!=null)
            {
                user.setAddresses(dto.getAddresses().stream().map(addressDto -> {
                    user.getAddresses().clear(); // clear old addresses
                    Address address = convertToAddressEntity(addressDto);
                    address.setUser(user);
                    return address;
                }).toList());
            }
            return user;
        }).orElseThrow(()->new RuntimeException("User with id doesn't exists"));
        userRepository.save(updatedUser);
        return convertToUserDto(updatedUser);
    }

    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToUserDto).orElseThrow(() -> new RuntimeException("User with this email doesn't exist"));
    }

    public UserDto getUserByAuthServiceId(Long authServiceId) {
        return userRepository.findByAuthUserId(authServiceId).map(this::convertToUserDto).orElseThrow(() -> new UserWithIdNotFound("Invalid authId"));

    }

    private User convertToUserEntity(UserDto dto) {
        if (dto == null)
            return null;
        User u = User.builder()
                .email(dto.getEmail()).name(dto.getName())
                .phone(dto.getPhone()).authUserId(dto.getAuthUserId()).build();
        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            u.setAddresses(dto.getAddresses().stream().map(addressDto -> {
                Address address = convertToAddressEntity(addressDto);
                address.setUser(u);
                return address;
            }).toList());
        }
        return u;

    }

    private Address convertToAddressEntity(AddressDto addressDto) {
        if (addressDto == null)
            return null;
        return Address.builder().state(addressDto.getState())
                .city(addressDto.getCity()).country(addressDto.getCountry())
                .tag(addressDto.getTag()).street(addressDto.getStreet()).status(addressDto.getStatus())
                .postalCode(addressDto.getPostalCode()).build();
    }

    private UserDto convertToUserDto(User user) {
        UserDto dto = UserDto.builder().name(user.getName())
                .phone(user.getPhone()).email(user.getEmail()).id(user.getId())
                .authUserId(user.getAuthUserId())
                .build();
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            dto.setAddresses(user.getAddresses().stream().map(this::convertToAddressDto).toList());
        }
        return dto;
    }

    private AddressDto convertToAddressDto(Address address) {
        if (address == null)
            return null;
        return AddressDto.builder().city(address.getCity())
                .state(address.getState()).postalCode(address.getPostalCode())
                .status(address.getStatus())
                .country(address.getCountry()).build();

    }


}

