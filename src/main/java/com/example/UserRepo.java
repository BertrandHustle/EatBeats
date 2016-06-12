package com.example;

import org.springframework.data.repository.CrudRepository;

/**
 * repo interface for User class
 */
public interface UserRepo extends CrudRepository<User, Integer>{
}
