package com.crooks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import twitter4j.*;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws TwitterException, IOException {
        Twitter twitter = TwitterFactory.getSingleton();
        //Status status = twitter.updateStatus("Hello World! testing, testing, testies, 1, 2 ...3?");
        Query query = new Query("FollyBeach");
        QueryResult results = twitter.search((query));

        for(Status tweet : results.getTweets()){
            System.out.println(tweet.getUser().getScreenName() + ":" + tweet.getText() );
        }


        Document doc = Jsoup.connect("http://www.surfline.com/surf-report/folly-beach-pier-southside-southeast_5294/").get();
        Element content = doc.getElementById("current-surf-range");


        System.out.println(content);
    }
}
