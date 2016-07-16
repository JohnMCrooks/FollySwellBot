package com.crooks;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Main {

    public static void main(String[] args) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        Status status = twitter.updateStatus("Hello World! testing, testing, testies, 1, 2 ...3?");
        System.out.println("done");
    }
}
