package com.example;

import com.example.PasswordHasher;
import com.example.User;
import com.example.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for User-related methods
 */

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    public void createAndStoreUser (String username, String password) throws PasswordHasher.CannotPerformOperationException {
        User user = new User(username, password);
        userRepo.save(user);
    }

}
