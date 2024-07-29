package com.card.Credit_card.services;


import com.card.Credit_card.dto.SignUpDto;
import com.card.Credit_card.dto.UserDto;
import com.card.Credit_card.entities.User;
import com.card.Credit_card.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Returns the user based on the email id.
     *
     * @param emailId the email id of the user.
     * @return user details
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        logger.trace("Entered loadUserByUsername");
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new
                        UsernameNotFoundException(String.format("User with email %s not found.", emailId)));
        logger.trace("Exited loadUserByUsername");
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(emailId)
                .password(user.getPassword())
                .authorities(Set.of())
                .disabled(!user.isActive())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    /**
     * Sign up user optional.
     *
     * @param signUpDTO the sign up dto
     * @return the optional
     */
    public Optional<UserDto> signUpUser(@Valid SignUpDto signUpDTO) {
        logger.trace("Entered signUpUser");
        User user = modelMapper.map(signUpDTO, User.class);
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        User registeredUser = userRepository.save(user);
        UserDto userDTO = modelMapper.map(registeredUser, UserDto.class);
        logger.trace("Exited signUpUser");
        return Optional.of(userDTO);
    }

    /**
     * Gets user by id.
     *
     * @param userId the user id
     * @return the user by id
     */
    public Optional<UserDto> getUserById(UUID userId) {
        logger.trace("Entered getUserById");
        Optional<User> optionalUser = userRepository.findById(userId);
        logger.trace("Exited getUserById");
        return optionalUser.map(user -> modelMapper.map(user, UserDto.class));
    }

    public Optional<UserDto> getUserByEmailId(String emailId) {
        logger.trace("Entered getUserByEmailId");
        Optional<User> optionalUser = userRepository.findByEmailId(emailId);
        logger.trace("Exited getUserByEmailId");
        return optionalUser.map(user -> modelMapper.map(user, UserDto.class));
    }


    /**
     * Delete user by id.
     *
     * @param id the id
     */
    public void deleteUserById(UUID id) {
        logger.trace("Entered deleteUserById");
        userRepository.deleteById(id);
        logger.trace("Exited deleteUserById");
    }

    /**
     * Checks if user exists or not based on email id.
     *
     * @param emailId the email id
     * @return the boolean
     */
    public boolean isUserExists(String emailId) {
        return userRepository.findByEmailId(emailId).isPresent();
    }

    /**
     * Checks if user exists or not based on userId.
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean isUserExists(UUID userId) {
        return userRepository.findById(userId).isPresent();
    }
}
