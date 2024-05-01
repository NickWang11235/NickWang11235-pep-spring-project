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

import com.example.entity.Message;
import com.example.entity.Account;
import com.example.exception.ClientFormatException;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.IncorrectPasswordException;
import com.example.exception.MessageDoesNotExistErrorException;
import com.example.exception.MessageDoesNotExistOKException;
import com.example.exception.AccountDoesNotExistException;
import com.example.service.MessageService;
import com.example.service.AccountService;
import com.example.utils.Utils;

/**
 * controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use
 * the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations.
 * You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
@RestController
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * GET endpoint that retrieves all messages
     * status 200 always
     * 
     * @return List<Message> the list of existing messages. Empty list if no message
     *         exists
     */
    @GetMapping(value = "messages")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Message> getAllMessages() {
        return messageService.findAll();
    }

    /**
     * GET endpoint that retrieves all message of account with account_id
     * status 200 always
     * 
     * @param account_id
     * @return List<Message> the list of messages of account with account_id. Empty
     *         list if account has no messages, or no account with account_id exists
     */
    @GetMapping(value = "accounts/{account_id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Message> getMessagesFromUser(@PathVariable int account_id) {
        return messageService.findByPostedBy(account_id);
    }

    /**
     * GET endpoint that retrieves a message by message_id
     * status 200 always
     * 
     * @param message_id
     * @return Message the message with message_id if exists, null otherwise
     */
    @GetMapping(value = "messages/{message_id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Message getMessageByID(@PathVariable int message_id) {
        return messageService.findByMessageId(message_id);
    }

    /**
     * POST endpoint that registers an account. Request body should contain JSON of
     * account to be inserted into the database
     * validates username and password format, then checks if username is taken
     * status 400 if format is illformed. Status 409 if username is taken
     * status 200 if successful. Returns registered account
     * 
     * @param account new account to be registered. Does not contain account_id
     * @return Account the account just registered. Contains account_id
     * @throws ClientFormatException      exception thrown when username or password
     *                                    are illformed. status 400
     * @throws DuplicateUsernameException exception thrown when username is already
     *                                    taken. status 409
     */
    @PostMapping(value = "register")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Account postRegisterAccount(@RequestBody Account account)
            throws ClientFormatException, DuplicateUsernameException {
        // validates username and password format
        if (!Utils.validateAccountFormat(account)) {
            throw new ClientFormatException();
        }
        // verify username has not been taken
        if (accountService.findByUsername(account.getUsername()) != null) {
            throw new DuplicateUsernameException();
        }
        return accountService.save(account);
    }

    /**
     * POST endpoint that logs an account in. Request body should contain JSON of
     * account to be logged in
     * verifies account exists in database and password is correct
     * status 401 if login fails
     * status 200 if successful. Returns logged in account
     * 
     * @param account the account to login with. Does not contain account_id
     * @return Account the account just logged in. Contains account_id
     * @throws IncorrectPasswordException   exception thrown when password does not
     *                                      match
     * @throws AccountDoesNotExistException exception thrown when account does not
     *                                      exist
     */
    @PostMapping(value = "login")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Account postLoginAccount(@RequestBody Account account)
            throws IncorrectPasswordException, AccountDoesNotExistException {
        // retrieves account
        Account registeredAccount = accountService.findByUsername(account.getUsername());
        // checks if exists
        if (registeredAccount == null) {
            throw new AccountDoesNotExistException();
        }
        // checks password
        if (!registeredAccount.getPassword().equals(account.getPassword())) {
            throw new IncorrectPasswordException();
        }
        return registeredAccount;
    }

    /**
     * POST endpoint that creates a new message. Request body should contain JSON of
     * the new message to be inserted into the database
     * validates message format, checks it is posted by an existing account
     * status 400 if message creation fails in any way
     * status 200 if successful. Returns created message
     * 
     * @param message the message to create. Does not contain message_id
     * @return Message the message created. Contains message_id
     * @throws ClientFormatException exception thrown if message is illformed, or
     *                               the message is posted by an non-existing
     *                               account
     */
    @PostMapping(value = "messages")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Message postCreateMessage(@RequestBody Message message) throws ClientFormatException {
        // validates message format
        if (!Utils.validateMessageFormat(message)) {
            System.out.println("bad message");
            throw new ClientFormatException();
        }
        // retrieves posted by account
        Account postedBy = accountService.findByAccountId(message.getPostedBy());
        // verifies user exists
        if (postedBy == null) {
            throw new ClientFormatException();
        }
        return messageService.save(message);
    }

    /**
     * PATCH endpoint that updates an existing message with new text. Request body
     * should contain the message to replace the existing message
     * validates new message format and checks if old message exists then updates
     * only the text of the existing message
     * status 400 if message update fails in any way
     * status 200 if successful
     * 
     * @param message_id the message to be patched
     * @param message    the message to replace the existing message
     * @return int number of message patched. This should always be 1 if patch is
     *         successful
     * @throws ClientFormatException             exception thrown if message format
     *                                           is illformed
     * @throws MessageDoesNotExistErrorException exception thrown if old message
     *                                           does not exist. This exception
     *                                           results in status 400
     */
    @PatchMapping(value = "messages/{message_id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody int patchMessageById(@PathVariable int message_id, @RequestBody Message message)
            throws ClientFormatException, MessageDoesNotExistErrorException {
        // validates message format
        if (!Utils.validateMessageFormat(message)) {
            throw new ClientFormatException();
        }
        // retrieves old message
        Message oldMessage = messageService.findByMessageId(message_id);
        // checks if old message exist
        if (oldMessage == null) {
            throw new MessageDoesNotExistErrorException();
        }
        oldMessage.setMessageText(message.getMessageText());
        messageService.save(oldMessage);
        // since message_id is a primary key thus unique, always return 1 if patch is
        // successful
        return 1;
    }

    /**
     * DELETE endpoint that deletes a message by message_id
     * deletes a message and returns the number of rows deleted in the database
     * returns empty response body if message does not exist
     * status 200 always
     * 
     * @param message_id
     * @return int number of rows deleted. This is always 1 if deletion is
     *         successful
     * @throws MessageDoesNotExistOKException exception thrown when message does not
     *                                        exist. This exception still restuls in
     *                                        a status 200
     */
    @DeleteMapping(value = "messages/{message_id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody int deleteMessageByID(@PathVariable int message_id) throws MessageDoesNotExistOKException {
        // checks if message with message_id exists
        if (messageService.findByMessageId(message_id) == null) {
            throw new MessageDoesNotExistOKException();
        }
        return messageService.deleteById(message_id);
    }

    /**
     * status 200
     */
    @ExceptionHandler(MessageDoesNotExistOKException.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody void handleOKException() {
    }

    /**
     * status 400
     */
    @ExceptionHandler({ ClientFormatException.class, MessageDoesNotExistErrorException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody void handleBadRequestException() {

    }

    /**
     * status 401
     */
    @ExceptionHandler({ IncorrectPasswordException.class, AccountDoesNotExistException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody void handleUnauthorizedException() {
    }

    /**
     * status 409
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody void handleConflictException() {
    }

}