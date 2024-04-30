package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.ClientFormatException;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.IncorrectPasswordException;
import com.example.exception.MessageDoesNotExistException;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.example.utils.Utils;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }


    @PostMapping(value = "register")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Account userRegistration(@RequestBody Account account) throws ClientFormatException, DuplicateUsernameException{
        if(!Utils.validateAccountFormat(account)){
            throw new ClientFormatException();
        }
        if(accountService.findByUsername(account.getUsername()) != null){
            throw new DuplicateUsernameException();
        }
        return account;
    }

    @ExceptionHandler(ClientFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody void handleBadRequestException(){

    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody void handleConflictException(){

    }

    @PostMapping(value = "login")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Account login(@RequestBody Account account) throws IncorrectPasswordException{
        Account registeredAccount = accountService.findByUsername(account.getUsername());
        if(!registeredAccount.getPassword().equals(account.getPassword())){
            throw new IncorrectPasswordException();
        }
        return registeredAccount;
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody void handleUnauthorizedException(){

    }

    @PostMapping(value = "messages")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Message createNewMessage(@RequestBody Message message) throws ClientFormatException{
        if(!Utils.validateMessageFormat(message)){
            System.out.println("bad message");
            throw new ClientFormatException();
        }
        Account postedBy = accountService.findByAccountId(message.getPostedBy());
        if(postedBy == null){

            System.out.println("bad user");
            throw new ClientFormatException();
        }
        return messageService.save(message);
    }

    @GetMapping(value = "messages")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Message> getAllMessages(){
        return messageService.findAll();
    }

    @GetMapping(value = "messages/{message_id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Message getMessageWithID(@PathVariable int message_id){
        return messageService.findByMessageId(message_id);
    }

    @DeleteMapping(value = "messages/{message_id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody int deleteMessageWithID(@PathVariable int message_id) throws MessageDoesNotExistException{
        if(messageService.findByMessageId(message_id) == null){
            throw new MessageDoesNotExistException();
        }
        return messageService.deleteMessage(message_id);
    }

    @ExceptionHandler(MessageDoesNotExistException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody void handleOKException(){

    }

    @PatchMapping(value = "messages/{message_id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody int updateMessageWithID(@PathVariable int message_id, @RequestBody Message message) throws ClientFormatException{
        if(!Utils.validateMessageFormat(message)){
            throw new ClientFormatException();
        }
        Message oldMessage = messageService.findByMessageId(message_id);
        if(oldMessage == null){
            throw new ClientFormatException();
        }
        oldMessage.setMessageText(message.getMessageText());
        messageService.save(oldMessage);
        return 1;
    }

    @GetMapping(value = "accounts/{account_id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Message> getMessagesFromUser(@PathVariable int account_id){
        return messageService.findByPostedBy(account_id);
    }

}
