package com.mindhub.homebanking.utils;

public final class CardUtil {

    public static String randomCardNumber(){
        Integer numCard, max=9999, min = 1;
        String numCardString="";
        for (int i=0; i<4 ;i++){
            numCard = (int)((Math.random() * (max - min)) + min);
            if (i ==0 ){
                numCardString =  numCard.toString();
            }else {
                numCardString += ("-" + numCard.toString());
            }
        }
        return numCardString;
    }
}
