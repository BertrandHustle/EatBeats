package com.example;

import com.example.User;
import org.springframework.data.repository.CrudRepository;

/**
 * repo interface for User class
 */
public interface UserRepo extends CrudRepository<User, Integer>{
    User findFirstByUsername(String name);
}
