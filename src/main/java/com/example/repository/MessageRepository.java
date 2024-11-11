package com.example.repository;
import com.example.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Method to find messages by account ID (postedBy field)
    List<Message> findByPostedBy(int accountId);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE message", nativeQuery = true)
    void truncateTable();
}
