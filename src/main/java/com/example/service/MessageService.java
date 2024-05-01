package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Message findByMessageId(int message_id) {
        return messageRepository.findByMessageId(message_id);
    }

    public int deleteById(int message_id) {
        messageRepository.deleteById(message_id);
        return 1;
    }

    public List<Message> findByPostedBy(Integer account_id) {
        return messageRepository.findByPostedBy(account_id);
    }

}
