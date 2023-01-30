package com.cydeo.service;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;

import java.util.List;

public interface RoleService {

    RoleDto findById(Long id);

    List<RoleDto> listAllRoles();

    List<RoleDto> listRoleByLoggedInUser();

}
