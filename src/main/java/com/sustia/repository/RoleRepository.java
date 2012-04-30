package com.sustia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sustia.domain.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
}
