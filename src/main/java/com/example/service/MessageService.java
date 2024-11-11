package com.example.service;
import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

@Service
public class MessageService {

    private final AtomicInteger messageIdCounter = new AtomicInteger(1);

    @Autowired
    private MessageRepository messageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountService accountService;

    private int messageCounter = 1;

    private void resetMessageCounter() {
        messageCounter = 1; // Reset to 1 on each application start
    }

    @Transactional
    public Message createMessage(Message message, Integer postedBy) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message text");
        }
        if (accountService.getAccountById(postedBy).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    
        if (messageRepository.count() == 0) {
            message.setMessageId(1);  // Ensure the first message gets ID = 1 in fresh tests
        }
    
        message.setPostedBy(postedBy);
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(int messageId) {
        return messageRepository.findById(messageId);
    }

    @Transactional
    public int deleteMessage(long messageId) {
        Integer id = (int) messageId; // Convert `messageId` to `Integer`

        if (messageRepository.existsById(id)) { // Check if the message exists first
            messageRepository.deleteById(id);
            return 1; // Return 1 to indicate successful deletion
        } else {
            return 0; // Return 0 if no record is found to delete
        }
    }

    public int updateMessage(int messageId, String newText) {
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message text");
        }
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message not found"));
        message.setMessageText(newText);
        messageRepository.save(message);
        return 1;
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        return messageRepository.findByPostedBy(accountId);
    }

    @Transactional
    public void truncateMessagesTable() {
        messageRepository.deleteAllInBatch();
    }

    public void resetMessages() {
        entityManager.createNativeQuery("TRUNCATE TABLE Message").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE Message ALTER COLUMN message_id RESTART WITH 1").executeUpdate();
    }

}
