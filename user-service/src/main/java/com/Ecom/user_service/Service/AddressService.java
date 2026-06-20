package com.Ecom.user_service.Service;

import com.Ecom.user_service.Dto.AddressDto;
import com.Ecom.user_service.Entity.Address;
import com.Ecom.user_service.Entity.User;
import com.Ecom.user_service.Exception.AddressWithIdNotFound;
import com.Ecom.user_service.Repository.AddressRepository;
import com.Ecom.user_service.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final Logger logger= LoggerFactory.getLogger(AddressService.class);
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    public AddressDto addAddress(Long userId, AddressDto dto) {
        logger.info("Going to map dto to entity");
        Address address = convertToEntity(dto);
        logger.info("Entity mapped successfully");
        logger.info("Checking whether user exist  or not");
        userRepository.findById(userId).ifPresent(address::setUser);
        if(address.getUser()!=null)
        {
            logger.info("User exxisit saving address for him");
            return convertToDto(addressRepository.save(address));

        }
        throw new RuntimeException("Cannot saved address as user doesn't exisit");
    }

    public AddressDto getAddress(Long addressId) {
        return addressRepository.findById(addressId).map(address -> {
            return convertToDto(address);
        }).orElseThrow(() -> new AddressWithIdNotFound("No address with this id exist"));
    }

    public List<AddressDto> getAllUserAddress(Long userId) {
        return addressRepository.findByUserId(userId).orElseGet(List::of).stream().map(this::convertToDto
        ).toList();
    }

    public void delete(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        User user = address.getUser();      // get parent user
        user.getAddresses().remove(address); // remove from collection
        userRepository.save(user);
    }

    public AddressDto update(Long addressId, AddressDto addressDto) {
       return addressRepository.findById(addressId).map(address -> {
            address.setCity(addressDto.getCity());
            address.setState(addressDto.getState());
            address.setCountry(addressDto.getCountry());
            address.setPostalCode(addressDto.getPostalCode());
            address.setTag(addressDto.getTag());
            address.setStatus(addressDto.getStatus());
            address.setStreet(addressDto.getStreet());
            return convertToDto(address);
        }).orElseThrow(()->new AddressWithIdNotFound("No address exists with Id"));
    }

    private Address convertToEntity(AddressDto addressDto) {
        return Address.builder().status(addressDto.getStatus()).
                city(addressDto.getCity()).country(addressDto.getCountry()).
                state(addressDto.getState()).street(addressDto.getStreet()).
                postalCode(addressDto.getPostalCode()).tag(addressDto.getTag()).build();
    }

    private AddressDto convertToDto(Address address) {
        return AddressDto.builder().status(address.getStatus()).
                city(address.getCity()).country(address.getCountry()).id(address.getId())
                        .street(address.getStreet()).state(address.getState()).
                postalCode(address.getPostalCode()).tag(address.getTag()).build();
    }

}
