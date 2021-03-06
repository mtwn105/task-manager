package com.cardinity.taskmanager.service;

import com.cardinity.taskmanager.config.security.JwtProvider;
import com.cardinity.taskmanager.dao.RoleRepository;
import com.cardinity.taskmanager.dao.UserRepository;
import com.cardinity.taskmanager.entity.Role;
import com.cardinity.taskmanager.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private JwtProvider jwtProvider;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Sign in a user into the application, with JWT-enabled authentication
     *
     * @param username  username
     * @param password  password
     * @return Optional of the Java Web Token, empty otherwise
     */
    public Optional<String> signin(String username, String password) {
        LOGGER.info("New user attempting to sign in");
        Optional<String> token = Optional.empty();
        Optional<User> user = userRepository.findByUserName(username);
        if (user.isPresent()) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                token = Optional.of(jwtProvider.createToken(username, user.get().getRoles()));
                LOGGER.info("Log in success for user {}", username);
//            }
        }
        return token;
    }

    /**
     * Create a new user in the database.
     *
     * @param username username
     * @param password password
     * @param firstName first name
     * @param lastName last name
     * @return Optional of user, empty if the user already exists.
     */
    public Optional<User> signup(String username, String password, String firstName, String lastName) {
        LOGGER.info("New user attempting to sign in");
        Optional<User> user = Optional.empty();
        if (!userRepository.findByUserName(username).isPresent()) {
            Optional<Role> role = roleRepository.findByRoleName("ROLE_USER");
            user = Optional.of(userRepository.save(new User(username,
                            passwordEncoder.encode(password),
                            role.get(),
                            firstName,
                            lastName)));
        }
        return user;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUserById(@NotNull long userId) throws UsernameNotFoundException{
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not with provided id."));

        return user;

    }

}
