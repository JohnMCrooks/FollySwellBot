package com.crooks;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import com.github.sarxos.webcam.ds.ipcam.impl.IpCamHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import twitter4j.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;


public class Main {

    static TemporalUnit minutes = ChronoUnit.MINUTES;
    static TemporalUnit hours = ChronoUnit.HOURS;
    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public static void main(String[] args) throws TwitterException, IOException, InterruptedException {
        boolean cantStopThisTrain = true;
        String lastTweet = null;

        while (cantStopThisTrain == true) {
            MSWprop secretKey = new MSWprop();
            String url = "http://magicseaweed.com/api/" + secretKey.key + "/forecast/?spot_id=672";
            Instant now = Instant.now();

            String rawJson = grabJson(url);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode mainJson = mapper.readValue(rawJson, JsonNode.class);
            ArrayList<SwellPeriod> swellArray = new ArrayList<>();


            //break the information out of json into class objects
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


            LocalTime currentTime = LocalTime.now().truncatedTo(minutes);
            if (currentTime.isAfter(LocalTime.NOON)){
                currentTime = currentTime.minus(12, hours);
            }
            int counter = 0;
            while (counter < swellArray.size() - 1) {
                if (now.getEpochSecond() <= swellArray.get(counter + 1).getUnixTime() && now.getEpochSecond() > swellArray.get(counter).getUnixTime()) {
                    String tweetFormmated = String.format("%s - Swell Height: %d-%d ft. with winds at %d mph out of the %s #FollyBeach #surfing #Charleston #SurfReport #MagicSeaWeed",
                            currentTime ,swellArray.get(counter + 1).getMinHeight(), swellArray.get(counter + 1).getMaxHeight(), swellArray.get(counter + 1).getWindSpeed(), swellArray.get(counter + 1).getWindDirection());
                    System.out.println(tweetFormmated);

                    //captureImage();
//                    sendTweet(tweetFormmated);
                    counter++;
                } else {
                    counter++;
                }
            }
            Thread.sleep(10800000);
        }
    } // End Main Method

    public static void sendTweet(String tweetFormmated) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        Status status = twitter.updateStatus(tweetFormmated);
        System.out.println("The People have been Informed.");
    }

//    public static void captureImage(){
//        String follyCamURL= "http://208.43.68.139/surfchex/follybeach-super/playlist.m3u8";
//        Instant instant = Instant.now();
//
//        try{  IpCamDeviceRegistry.register("Folly", follyCamURL, IpCamMode.PUSH);
//        IpCamDeviceRegistry.isRegistered(follyCamURL);
//        }
//        catch (IOException e){
//          System.err.println("IpCam Registration Failed");
//        }
//        IpCamDevice device = new IpCamDevice();
//
//        BufferedImage image = device.getImage();
//        try {
//            ImageIO.write(image, "PNG", new File(instant+".png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("File captured and saved at " + instant.toString());
//    }

    public static String grabJson(String mswUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(mswUrl).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
