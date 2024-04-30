package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    MessageRepository messageRepository;
    @Autowired
    public MessageService(MessageRepository messageRepository){
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
    public int deleteMessage(int message_id) {
        return messageRepository.deleteByMessageId(message_id);
    }
    public List<Message> findByPostedBy(int account_id) {
        return messageRepository.findByPostedBy(account_id);
    }

}
