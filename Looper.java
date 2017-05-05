/**
 * Created by John Hale on 5/1/2017.
 */
import org.joda.time.LocalDateTime;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Looper {
    BigDecimal cash = new BigDecimal(1000000);
    BigDecimal tradeCost = new BigDecimal(5);
    int numOfTslaStock=0;
    int numOfFbStock=0;
    int numOfAppleStock=0;
    int numOfBacStock=0;
    int counter = 0;
    double[] tesla = new double[3];
    double[] apple = new double[3];
    double[] fb = new double[3];
    double[] boa = new double[3];
/*
*
*
*
*
*
*
*
 */
    public void callTweets()  {
        BigDecimal totalWorth = new BigDecimal(0);
        ProcessTweets tweetSent = new ProcessTweets();
        StockData gp = new StockData();
       // Twitter twitter = new TwitterFactory().getInstance();

        HashMap<String, BigDecimal> prices = new HashMap<String, BigDecimal>();
        try {
            prices = gp.getStocksPrice();
        }
        catch (IOException e){
            System.out.println("io error");
        }
        if (this.counter == 2) {
            this.tesla[0] = this.tesla[1];
            this.tesla[1] = this.tesla[2];
            this.apple[0] = this.apple[1];
            this.apple[1] = this.apple[2];
            this.fb[0] = this.fb[1];
            this.fb[1] = this.fb[2];
            this.boa[0] = this.boa[1];
            this.boa[1] = this.boa[2];
        }

        this.tesla[this.counter] = tweetSent.processTweet("Tesla","C:/Users/John Hale/Desktop/twitterTesla.txt", "TSLA");
        this.apple[this.counter] = tweetSent.processTweet("Apple","C:/Users/John Hale/Desktop/twitterApple.txt","AAPL");
        this.fb[counter] = tweetSent.processTweet("Facebook","C:/Users/John Hale/Desktop/twitterFacebook.txt","FB");
        this.boa[counter] = tweetSent.processTweet("Bank of America","C:/Users/John Hale/Desktop/twitterBankOA.txt","BAC");
       LocalDateTime ldt = LocalDateTime.now();

        if (ldt.getDayOfWeek() <  6 && this.counter == 2) {
            String histTesla = "C:/Users/John Hale/Desktop/TeslaHistory.txt";

            if (9 <= ldt.getHourOfDay() && ldt.getHourOfDay() <= 10) {
                makeTrades(this.tesla, 'T');
                makeTrades(this.apple, 'A');
                makeTrades(this.fb,'F');
                makeTrades(this.boa,'B');

                //OPENING TRADE
            }
            else if (12 <= ldt.getHourOfDay() && ldt.getHourOfDay() <= 1) {
                makeTrades(this.tesla, 'I');
                makeTrades(this.apple, 'A');
                makeTrades(this.fb,'F');
                makeTrades(this.boa, 'B');                //NOON TRADE
            }
            else if (2 <= ldt.getHourOfDay() && ldt.getHourOfDay() <= 3) {
                makeTrades(this.tesla, 'T');
                makeTrades(this.apple, 'A');
                makeTrades(this.fb, 'A');
                makeTrades(this.boa, 'B');                //CLOSING TRADE
            }
        }
        if (this.counter < 2) {
            ++this.counter;
        }

        BigDecimal bacPrice = prices.get("BAC");
        BigDecimal applePrice = prices.get("AAPL");
        BigDecimal fBPrice = prices.get("FB");
        BigDecimal teslaPrice = prices.get("TSLA");
        BigDecimal bac = new BigDecimal(this.numOfBacStock);
        BigDecimal tsla = new BigDecimal(this.numOfTslaStock);
        BigDecimal fb = new BigDecimal(this.numOfFbStock);
        BigDecimal aapl = new BigDecimal(this.numOfAppleStock);

        totalWorth = totalWorth.add(this.cash);
        totalWorth = totalWorth.add(bacPrice.multiply(bac));
        totalWorth = totalWorth.add( applePrice.multiply(aapl));
        totalWorth = totalWorth.add(fBPrice.multiply(fb));
        totalWorth = totalWorth.add(teslaPrice.multiply(tsla));

        // total return = (totalWorth / 1,000,000) * 100 - 100 == totalWorth / 10,000 - 100
        float totalReturn = (totalWorth.floatValue() / 10000 ) - 100;
        String portfolio = "C:/Users/John Hale/Desktop/totalWorth.txt";
        try{
            FileWriter writer = new FileWriter(portfolio, true);
            writer.write(totalWorth.floatValue() + "," + totalReturn + System.getProperty("line.separator"));
            writer.close();
        } catch (IOException e) {
            // do something
            System.out.println("IO Error");
        }

    }
    /*
    *
    *
    *
    *
    *
    *
    *
    *
     */
    public void makeTrades(double[] data, char ticker) {
        StockData sd = new StockData();
        int numOfStocks = 0;
        BigDecimal sell = new BigDecimal(5000);
        BigDecimal bet1;
        if (data[2] >= 3) {
             bet1 = new BigDecimal(25000);
        }
        else if (data[2] >= 2){
             bet1 = new BigDecimal(15000);

        }
        else if (data[2] >= 1) {
             bet1 = new BigDecimal(5000);
        }
        else {
            bet1 = new BigDecimal(1000);
        }
        HashMap<String,BigDecimal> prices = null;
        try {
            prices = sd.getStocksPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tradeFeed = "C:/Users/John Hale/Desktop/tradeFeed.txt";


        if (data[0] <= data[1] && data[1] <= data[2]) { //BUY
            switch (ticker){
                case 'T':
                    //bet 25k
                    BigDecimal tPrice = prices.get("TSLA");
                    numOfStocks = (int) Math.floor(bet1.divide(tPrice).floatValue());
                    BigDecimal numOfStocksbig = new BigDecimal(numOfStocks);
                    BigDecimal cost = tPrice.multiply(numOfStocksbig).add(tradeCost);
                    if (this.cash.compareTo(cost) == 1){
                        this.cash = this.cash.subtract(cost);
                        this.numOfTslaStock+=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Bought" + numOfStocks + "TSLA" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
                case 'F':
                    //bet 25k
                    BigDecimal fPrice = prices.get("FB");
                    numOfStocks = (int) Math.floor(bet1.divide(fPrice).floatValue());
                    numOfStocksbig = new BigDecimal(numOfStocks);
                    cost = fPrice.multiply(numOfStocksbig).add(tradeCost);
                    if (this.cash.compareTo(cost) == 1){
                        this.cash = this.cash.subtract(cost);
                        this.numOfFbStock+=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Bought" + numOfStocks + "FB" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
                case 'A':
                    //bet 25k
                    BigDecimal aPrice = prices.get("AAPL");
                    numOfStocks = (int) Math.floor(bet1.divide(aPrice).floatValue());
                    numOfStocksbig = new BigDecimal(numOfStocks);
                    cost = aPrice.multiply(numOfStocksbig).add(tradeCost);
                    if (this.cash.compareTo(cost) == 1){
                        this.cash = this.cash.subtract(cost);
                        this.numOfAppleStock+=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Bought" + numOfStocks + "AAPL" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
                case 'B':
                    //bet 25k
                    BigDecimal bPrice = prices.get("BAC");
                    numOfStocks = (int) Math.floor(bet1.divide(bPrice).floatValue());
                    numOfStocksbig = new BigDecimal(numOfStocks);
                    cost = bPrice.multiply(numOfStocksbig).add(tradeCost);
                    if (this.cash.compareTo(cost) == 1){
                        this.cash = this.cash.subtract(cost);
                        this.numOfBacStock+=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Bought" + numOfStocks + "BAC" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
            }
        }
        else if (data[2] > data[1]) { //SELL
            switch (ticker){
                case 'T':
                    //sell 5k
                    BigDecimal tPrice = prices.get("TSLA");
                    numOfStocks = (int) Math.floor(sell.divide(tPrice).floatValue());
                    BigDecimal numOfStocksbig = new BigDecimal(numOfStocks);
                    BigDecimal cost = tPrice.multiply(numOfStocksbig).subtract(tradeCost);
                    if (this.numOfTslaStock >= numOfStocks){
                        this.cash = this.cash.add(cost);
                        this.numOfTslaStock-=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Sold" + numOfStocks + "TSLA" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
                case 'F':
                    //sell 5k
                    BigDecimal fPrice = prices.get("FB");
                    numOfStocks = (int) Math.floor(sell.divide(fPrice).floatValue());
                    numOfStocksbig = new BigDecimal(numOfStocks);
                    cost = fPrice.multiply(numOfStocksbig).subtract(tradeCost);
                    if (this.numOfFbStock >= numOfStocks){
                        this.cash = this.cash.add(cost);
                        this.numOfFbStock-=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Sold" + numOfStocks + "FB" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
                case 'A':
                    //sell 5k
                    BigDecimal aPrice = prices.get("AAPL");
                    numOfStocks = (int) Math.floor(sell.divide(aPrice).floatValue());
                    numOfStocksbig = new BigDecimal(numOfStocks);
                    cost = aPrice.multiply(numOfStocksbig).subtract(tradeCost);
                    if (this.numOfAppleStock >= numOfStocks){
                        this.cash = this.cash.add(cost);
                        this.numOfAppleStock-=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Sold" + numOfStocks + "AAPL" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }
                case 'B':
                    //sell 5k
                    BigDecimal bPrice = prices.get("BAC");
                    numOfStocks = (int) Math.floor(sell.divide(bPrice).floatValue());
                    numOfStocksbig = new BigDecimal(numOfStocks);
                    cost = bPrice.multiply(numOfStocksbig).subtract(tradeCost);
                    if (this.numOfBacStock >= numOfStocks){
                        this.cash = this.cash.add(cost);
                        this.numOfBacStock-=numOfStocks;
                    }
                    try{
                        FileWriter writer = new FileWriter(tradeFeed, true);
                        writer.write("Sold" + numOfStocks + "BAC" + "at" + cost +  System.getProperty("line.separator"));

                        writer.close();
                    } catch (IOException e) {
                        // do something
                        System.out.println("IO Error");
                    }

            }

        }
    }
/**
 *
 *
 *
 *
 *
 *
 *
 */
    public static void main(String[] args) {
        final Looper loop = new Looper();
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
        //    @Override
            public void run() {
                loop.callTweets();
            }
        }, 0, 1, TimeUnit.HOURS);
    }
}
