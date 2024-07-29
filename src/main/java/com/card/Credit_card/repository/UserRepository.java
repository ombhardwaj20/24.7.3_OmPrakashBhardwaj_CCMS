package com.card.Credit_card.repository;

import com.card.Credit_card.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Find by email id.
     *
     * @param emailId the email id
     * @return the user details if found
     */
    Optional<User> findByEmailId(String emailId);

    /**
     * Find by email id and password optional.
     *
     * @param emailId  the email id
     * @param password the password
     * @return the user details if found
     */
    Optional<User> findByEmailIdAndPassword(String emailId, String password);
}
