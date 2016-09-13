package com.crooks;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import twitter4j.*;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws TwitterException, IOException, InterruptedException {
        boolean cantStopThisTrain = true;
        Twitter twitter = new TwitterFactory().getSingleton();

        while (cantStopThisTrain == true) {
            MSWprop secretKey = new MSWprop();
            String url = "http://magicseaweed.com/api/" + secretKey.key + "/forecast/?spot_id=672";
            Instant now = Instant.now();

            //Oceanic forecasting can change rapidly, so I hit the API every time instead of retaining a single request and using each of the individually contained forecasts.
            String rawJson = grabJson(url);
            ObjectMapper mapper = new ObjectMapper();

            //Map the Json into a node
            JsonNode mainJson = mapper.readValue(rawJson, JsonNode.class);
            ArrayList<SwellPeriod> swellArray = new ArrayList<>();


            //break the information out of the main node into individual class objects and insert them into an ArrayList.
            for (JsonNode timeStamp : mainJson) {
                int minHeight = timeStamp.get("swell").findValue("minBreakingHeight").asInt();
                int maxheight = timeStamp.get("swell").findValue("maxBreakingHeight").asInt();
                long unixTime = timeStamp.findValue("localTimestamp").asInt();
                String windDirection = timeStamp.get("wind").findValue("compassDirection").asText();
                int windSpeed = timeStamp.get("wind").findValue("speed").asInt();
                SwellPeriod sp = new SwellPeriod(minHeight, maxheight, unixTime, windDirection, windSpeed);
                swellArray.add(sp);
            }

            //sort the array
            swellArray.stream().sorted((s1, s2) -> Integer.compare((int) s1.getUnixTime(), (int) s2.getUnixTime()));

            //Convert the time to non-military format
            LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
            String amPm = "am";
            if (currentTime.isAfter(LocalTime.NOON)){
                currentTime = currentTime.minus(12, ChronoUnit.HOURS);
                amPm = "pm";
            }

            int counter = 0;
            //Forecast time stamps are sent as unix time stamps, This compares the current time to the forecasts to grab the one following the current time.
            while (counter < swellArray.size() - 1) {
                if (now.getEpochSecond() <= swellArray.get(counter + 1).getUnixTime() && now.getEpochSecond() > swellArray.get(counter).getUnixTime()) {
                    String tweetFormmated = String.format("%s %s - Swell Height: %d-%d ft. with winds at %d mph out of the %s #FollyBeach #surfing #Charleston #SurfReport #MagicSeaWeed",
                            currentTime, amPm ,swellArray.get(counter + 1).getMinHeight(), swellArray.get(counter + 1).getMaxHeight(), swellArray.get(counter + 1).getWindSpeed(), swellArray.get(counter + 1).getWindDirection());

                    sendTweet(tweetFormmated);
                    followFollowers(twitter);
                    counter++;
                } else {
                    counter++;
                }
            }
            Thread.sleep(10800000);
        }
    } // End Main Method

    public static void sendTweet(String tweetFormmated) throws TwitterException, IOException {
        try{
            Twitter twitter = TwitterFactory.getSingleton();
            Status status = twitter.updateStatus(tweetFormmated);
            System.out.println(Instant.now().truncatedTo(ChronoUnit.MINUTES) + " - The People have been Informed.\n");
        }
        catch(TwitterException statusException){
            System.out.println("failed to send status update at: " + LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
            statusException.printStackTrace();
        }


    }// End SendTweet()

    public static void followFollowers(Twitter twitter) throws TwitterException {
        PagableResponseList<User> followersList = twitter.getFollowersList("FollySwellBot", -1);
        String[] myFollowerArray = new String[followersList.size()];

        int count=0;
        for (User user: followersList) {
            myFollowerArray[count] = user.getScreenName();
            count++;
        }
        ResponseList<Friendship> friendships = twitter.lookupFriendships(myFollowerArray);

        //Check each follower to see if it's mutual, If it's not grab their ID and follow them back.
        for (Friendship friendship: friendships){
            if (!friendship.isFollowing()){
                try{
                    twitter.createFriendship(friendship.getId());
                }catch (TwitterException addFriendFail){
                    addFriendFail.printStackTrace();
                    continue;
                }
            }
        }
    }//End followFollowers

    public static String grabJson(String mswUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(mswUrl).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        }catch (IOException mswException){
           mswException.printStackTrace();

        }
        return response.body().string();
    }//End grabJson()

}//End Main.java
