package com.processorservice.util.converters;

import com.processorservice.models.dtos.RegisterRequest;
import com.processorservice.models.dtos.UserDto;
import com.processorservice.models.dtos.UserPayload;
import com.processorservice.models.entities.User;

public class UserConverter {

    public static User convertDtoToEntity(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .walletAddress(userDto.getWalletAddress())
                .role(userDto.getRole())
                .phoneNumber(userDto.getPhoneNumber())
                .cityAddress(userDto.getCityAddress())
                .build();
    }

    public static UserDto convertEntityToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .walletAddress(user.getWalletAddress())
                .phoneNumber(user.getPhoneNumber())
                .cityAddress(user.getCityAddress())
                .institutionName(user.getInstitution().getName())
                .build();
    }

    public static User convertRegisterRequestToEntity(RegisterRequest registerRequest) {
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .walletAddress(registerRequest.getWalletAddress())
                .role(registerRequest.getRole())
                .phoneNumber(registerRequest.getPhoneNumber())
                .cityAddress(registerRequest.getCityAddress())
                .build();
    }

    public static UserPayload convertUserToUserPayload(User user) {
        return UserPayload.builder()
                .name(user.getName())
                .email(user.getEmail())
                .walletAddress(user.getWalletAddress())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .cityAddress(user.getCityAddress())
                .build();
    }
}
