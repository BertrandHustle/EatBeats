package com.example;

import com.example.User;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * repo interface for User class
 */
public interface UserRepo extends CrudRepository<User, Integer>{
    User findFirstByUsername(String name);
}
