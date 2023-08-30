package com.mindhub.homebanking.utils;


public final class Util {

    public static int getRandomNumber(Integer min, Integer max)  {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
