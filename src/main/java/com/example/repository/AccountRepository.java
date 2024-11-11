package com.example.repository;
import com.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    
    // Method to find an account by username (for registration uniqueness check)
    Optional<Account> findByUsername(String username);

    // Method to authenticate user by username and password
    Optional<Account> findByUsernameAndPassword(String username, String password);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE account", nativeQuery = true)
    void truncateTable();
}

