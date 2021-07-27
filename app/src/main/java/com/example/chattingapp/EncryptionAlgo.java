package com.example.chattingapp;

import com.example.chattingapp.AES.Decryption;
import com.example.chattingapp.AES.Encryption;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EncryptionAlgo {

    public String aesEncryptAlgo( String plainText) {
        String key = "Thats my Kung Fu";
        String enc = "";
        try {
            Encryption encryption = new Encryption(key, plainText);
            System.out.println("Encryption " + Encryption.final_encrypted);
            for(int i=0; i<Encryption.final_encrypted.size();i++){
                enc += Encryption.final_encrypted.get(i);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Encryption.final_encrypted.clear();
        return enc;
    }
    public String aesDecryptAlgo(String text){
        String key = "Thats my Kung Fu";
        String dec = "";
        List<String> data = new ArrayList<>();
        for(int i=0; i<text.length(); i+=2){
            data.add(text.substring(i,i+2));
        }

        Decryption decryption = new Decryption(data, key);

        for (int i = 0; i < decryption.decrypted_Text.length(); i += 2) {
            if (!(Integer.parseInt(decryption.decrypted_Text.substring(i, i + 2), 16) == 0)) {
                dec += String.valueOf((char) Integer.parseInt(decryption.decrypted_Text.substring(i, i + 2), 16));
            }
        }


        return dec;
    }
}
