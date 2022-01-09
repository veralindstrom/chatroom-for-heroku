package com.example.springboottutorial.id1212.entities.chat;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
    Role findRoleByRoleId(Integer roleId);
    Role findRoleByRole(String role);
}

