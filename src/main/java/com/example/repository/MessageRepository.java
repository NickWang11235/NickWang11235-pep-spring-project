package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    Message findByMessageId(int message_id);

    List<Message> findByPostedBy(int account_id);

}
