package com.example.id1212.ChatroomApp.entities.chat;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
    Role findRoleByRoleId(Integer roleId);
}

