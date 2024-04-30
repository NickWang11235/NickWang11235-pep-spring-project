package com.example.utils;

import com.example.entity.Account;
import com.example.entity.Message;

public class Utils {
    public static boolean validateMessageFormat(Message message){
        if(message.getMessageText().isBlank()){
            return false;
        }
        if(message.getMessageText().length() >= 256){
            return false;
        }
        return true;
    }

    public static boolean validateAccountFormat(Account account){
        if(account.getUsername().isBlank()){
            return false;
        }
        if(account.getPassword().length() < 4){
            return false;
        }
        return true;
    }
}
