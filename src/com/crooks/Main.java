package com.crooks;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.ws.api.message.Packet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import twitter4j.*;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;


public class Main {


    public static void main(String[] args) throws TwitterException, IOException {
        MSWprop secretKey = new MSWprop();
        String url = "http://magicseaweed.com/api/" + secretKey.key + "/forecast/?spot_id=672";

        String rawJson = grabJson(url);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode mainJson = mapper.readValue(rawJson, JsonNode.class);
        ArrayList<SwellPeriod> swellArray = new ArrayList<>();
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
        swellArray.stream()
                .sorted((s1,s2) -> Integer.compare((int) s1.getUnixTime(), (int) s2.getUnixTime()))
                .forEach(swell -> System.out.println(swell.getUnixTime()));

        Instant now = Instant.now();


        int counter = 0;
        while(counter < swellArray.size()-1){
            //loop through the array and find the two times the current time sits between.
            if (now.getEpochSecond() <= swellArray.get(counter+1).getUnixTime() && now.getEpochSecond()  >swellArray.get(counter).getUnixTime()){
                //sendTweet(swellArray);


                System.out.println("Swell Height: " + swellArray.get(counter+1).getMinHeight() + "-" +swellArray.get(counter+1).getMaxHeight() + "ft. with winds at " + swellArray.get(counter+1).getWindSpeed() + "mph from " + swellArray.get(counter+1).getWindDirection() );
                counter++;
            }else{
             counter++;
            }

        }



    } // End Main Method

    public static void sendTweet(ArrayList<SwellPeriod> swellArray) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        Status status = twitter.updateStatus("Current Conditions at the Folly Pier:");

    }

    public static String grabJson(String mswUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(mswUrl).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
