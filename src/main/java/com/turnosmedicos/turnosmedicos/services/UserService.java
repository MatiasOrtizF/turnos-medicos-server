package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.models.User;
import com.turnosmedicos.turnosmedicos.repositories.UserRepository;
import com.turnosmedicos.turnosmedicos.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User addUser(User user) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, user.getPassword());
        user.setPassword(hash);

        return userRepository.save(user);
    }

    public User getUserInfo(String token) {
        String userId = jwtUtil.getKey(token);
        if(userId!=null){
            return userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new ResourceNotFoundException("The user with this id: " + userId + "is not found"));
        } throw new UnauthorizedException("invalid token");
    }
}
