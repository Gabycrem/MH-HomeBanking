package com.mindhub.homebanking.utilitis;


public final class Utils {

    public static int getRandomAccountNumber(Integer min, Integer max)  {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
