package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.exception.RoleNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        return mapperUtil.convert(role, new RoleDto());
    }

    @Override
    public List<RoleDto> listAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> mapperUtil.convert(role, new RoleDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDto> listRoleByLoggedInUser() {

        if (securityService.getLoggedInUser().getRole().getId() == 1L) {
            return listAllRoles().stream().filter(roleDto -> roleDto.getId() == 2L).collect(Collectors.toList());
        }
        if (securityService.getLoggedInUser().getRole().getId() == 2L) {
            return listAllRoles().stream().filter(roleDto -> roleDto.getId() != 1L).collect(Collectors.toList());
        }
        return null;
    }
}
