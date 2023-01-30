package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, @Lazy SecurityService securityService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return mapperUtil.convert(user, new UserDto());
    }

    @Override
    public List<UserDto> listAllUsers() {
        return userRepository.findAll().stream().map(user -> mapperUtil.convert(user, new UserDto()))
                .sorted(Comparator.comparing((UserDto user) -> user.getCompany().getTitle())
                        .thenComparing(userDto -> userDto.getRole().getDescription()))
                .peek(userDto -> userDto.setOnlyAdmin(checkIfOnlyAdmin(userDto)))
                .collect(Collectors.toList());
    }

    private boolean checkIfOnlyAdmin(UserDto userDto) {
        if (!userDto.getRole().getDescription().equalsIgnoreCase("Admin")) return false;
        int countAdmins = userRepository.countAllByCompany_TitleAndRole_Description(userDto.getCompany().getTitle(), "Admin");
        return countAdmins == 1;
    }

    @Override
    public List<UserDto> listAllByLoggedInUser() {
        if (securityService.getLoggedInUser().getRole().getDescription().equals("Admin")) {
            return listAllUsers()
                    .stream().filter(userDto -> userDto.getCompany()
                            .getId().equals(securityService.getLoggedInUser()
                                    .getCompany().getId())).collect(Collectors.toList());
        } else if (securityService.getLoggedInUser().getRole().getDescription().equals("Root User")) {
            return listAllUsers().stream().
                    filter(userDto -> userDto.getRole().getDescription().equals("Admin")).collect(Collectors.toList());
        } else {
            throw new UserNotFoundException("No users is available");
        }
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserDto userDto = mapperUtil.convert(user, new UserDto());
        userDto.setOnlyAdmin(checkIfOnlyAdmin(userDto));
        return userDto;
    }

    @Override
    public void update(UserDto userDto) {
        User user1 = userRepository.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        User convertedUser = mapperUtil.convert(userDto, new User());
        convertedUser.setId(user1.getId());
        userRepository.save(convertedUser);
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setIsDeleted(true);
        user.setUsername(user.getUsername() + "-" + user.getId());
        userRepository.save(user);
    }

    @Override
    public void save(UserDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User user1 = mapperUtil.convert(user, new User());
        user1.setEnabled(true);
        userRepository.save(user1);
    }

    @Override
    public boolean isUsernameExist(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername()).orElse(null);
        if (user == null) return false;
        return !Objects.equals(userDto.getId(), user.getId());
    }
}
