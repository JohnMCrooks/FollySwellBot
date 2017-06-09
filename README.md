# FollySwellBot
This is a Twitter bot designed to broadcast the Surf report for Folly Beach SC. It is continually a work in progress, the Adam of my labours.


---

Latest Update: added text-message alerts for a private user via Twilio (in this case, myself, might roll this out to a wider audience if I get some time to set up a proper DB) 



|   | Still to be implemented (no particular order) |
|---| --- | 
| 1  | <del> Auto-Follow followers</del>  - Completed |
| 2  | New: Add server side configuration for text message alerts based on conditions (Set to > 3ft at the moment, this could get annoying during hurricane season and could use some tailoring without having to rebuild/deploy)| 
| 3  |Variation in messages based on surf height (e.g. "Grab your board, IT'S DOUBLE OVERHEAD!" versus)|
| 4  |Add real DB and expand Text alerts to multiple users |
| 4.5| Custom alerts for each user (based on conditions or possibly other factors [i.e. 4:30pm - end of day alert]) |
| 5  |Screenshots of the beach from a live cam - On hold due to lack of cameras in the area to use.|


---


###Configuration Note:


If deploying, this relies on a config.properties file in the same directory as the .jar file is being run in. 


The config file will need to have the following attributes: 


| Attribute  | Description |Expected Form|Example Value |
|---|
|  KEY | API Key provided by MagicSeaweed.com| String | 32charactErsgivenWillDoHere!#&a |
|  ACCOUNT_SID | Account_SID provided when you create an account with Twilio | String | 32charactErsgivenWillDoHere!#&a |
|  AUTH_TOKEN | Provided by Twilio when you create an account. | String | any32charactErs5Will6Do$21Here!# |
|  SENDER | Phone Number used to send messages - Also provided by Twilio | String | +12223334567 (International designation + Number) |
|  KEY | Phone Number of Recipeint (for the time being) | String | +12223334567 (International designation + Number) |



---


It Uses the MagicSeaWeed API and following libraries: Twilio, Twitter4j, OkHttp, and Jackson.

https://www.MagicSeaWeed.com

https://www.twilio.com/

http://twitter4j.org/en/

http://square.github.io/okhttp/

https://github.com/FasterXML/jackson

