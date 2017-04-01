package com.sales.service;

import java.io.IOException;

/**
 * Launcher for sales processor. Could also be launched in producer mode especially for testing purposes.
 * Created by Prashant on 01-04-2017.
 */
public class Run {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must have either 'producer' or 'consumer' as argument");
        }
        switch (args[0]) {
            case "producer":
                Producer.main(args);
                break;
            case "consumer":
                Consumer.main(args);
                break;
            default:
                throw new IllegalArgumentException("Invalid argument " + args[0]);
        }
    }

}
