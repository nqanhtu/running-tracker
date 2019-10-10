package com.runningtracker.common;


public class GenerateID {

    /**
     *@return: Time system id
    * */
    public String generateTimeID(){
        String str = String.valueOf(System.currentTimeMillis());
        return str;
    }
}
