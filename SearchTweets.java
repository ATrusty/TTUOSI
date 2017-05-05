/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by John C. Hale on April 13, 2017
 */

import twitter4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.conf.ConfigurationBuilder;
/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class SearchTweets {
    /**
     * Usage: java twitter4j.examples.search.SearchTweets [query]
     *
     * @param args search query
     */
    public static List<Status> getTweets(String args) {
        List<Status> totalTweets = new ArrayList<Status>();;

        //AUTHORIZATION
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("9WsyuIibex3iv0pJAx2LpIxPN")
                .setOAuthConsumerSecret("QtuA9HePOYwSCn8CI5XsyyCDSjDOmE31kkYybboSiwxqbJxpmp")
                .setOAuthAccessToken("827390718154936322-6LQ6BKxbpZwqVhOASWoacmTyn83pfoS")
                .setOAuthAccessTokenSecret("9j3eYIVqAj89dmZhI0MI09Cs7YMFz9FgbrbBHDgscIdQF");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();



        if (args.length() < 1) {
            System.out.println("java twitter4j.examples.search.SearchTweets [query]");
            System.exit(-1);
        }
 //       Twitter twitter = new TwitterFactory().getInstance();


        Map<String ,RateLimitStatus> status = new HashMap<String, RateLimitStatus>();



        try {
            Query query = new Query(args);
            QueryResult result;
            status = twitter.getRateLimitStatus();

            RateLimitStatus limitStat = status.get("/search/tweets");
            int remaining = limitStat.getRemaining();
            int remainingCounter = remaining;
            do {

                System.out.println(remainingCounter);
                  //  int secondsUntilReset = limitStat.getSecondsUntilReset();     IF we want to sleep
                if (args == "Tesla" && remainingCounter <= 60 ){
                        break;
                    }
                if (args == "Apple" && remainingCounter <= 40) {
                    break;
                }
                if (args == "Facebook" && remainingCounter <= 20) {
                    break;
                }
                if (args == "Bank of America" && remainingCounter == 0) {
                    break;
                }
                --remainingCounter;

                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                totalTweets.addAll(tweets);
                ///For Display Purposes Only
              //  System.out.println(result.getTweets());

            } while ((query = result.nextQuery()) != null);
            return totalTweets;
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        return totalTweets;
    }
    public static void main(String[] args) {
        String str = new String("Houston");

        getTweets(str);
    }
}
