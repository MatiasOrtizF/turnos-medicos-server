package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.models.LoginResponse;
import com.turnosmedicos.turnosmedicos.models.User;
import com.turnosmedicos.turnosmedicos.repositories.UserRepository;
import com.turnosmedicos.turnosmedicos.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;

    @Autowired
    public AuthService(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public boolean validationToken(String token) {
        String userId = jwtUtil.getKey(token);
        return (userId != null);
    }

    public User validationEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public LoginResponse validationCredentials(User user) {
        User userLogged = validationEmail(user.getEmail());
        if (userLogged != null) {
            String passwordHashed = userLogged.getPassword();

            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            if (argon2.verify(passwordHashed, user.getPassword())) {
                String tokenJWT = jwtUtil.create(userLogged.getId().toString(), userLogged.getEmail());

                LoginResponse response = new LoginResponse();
                response.setToken(tokenJWT);
                response.setUser(userLogged);

                return response;
            }
        }
        throw new RuntimeException("Email or password is incorrect");
    }

    public Long getUserId(String token) {
        String userId = jwtUtil.getKey(token);
        return Long.valueOf(userId);
    }
}
