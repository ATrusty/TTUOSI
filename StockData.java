/**
 * Created by sestens on 5/2/17.
 * Uses Yahoo Finance API from http://financequotes-api.com/
 */

import yahoofinance.YahooFinance;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class StockData {

    String[] tickers = {"AAPL", "BAC", "GOOG", "TSLA"};

    public static void main(String args[]) throws IOException {


        //Set up the call to check multiple stocks at once
        //String[] tickers = {"BAC", "GOOG", "AAPL", "BAC", "TSLA"};
        //Map<String,Stock> stocks = YahooFinance.get(tickers);
        //for (String s:stocks.keySet()){
        //    System.out.println(stocks.get(s).getQuote().getPrice());
        //}
        //System.out.println(stocks.get("TSLA").getQuote());
        //Testing the historical data stuff
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        //from.add(Calendar.YEAR, 0); // from 5 years ago
        from.add(Calendar.DATE,-4); //from 4 days ago
        //System.out.println("from = " + format1.format(from.getTime()));
        //System.out.println("to = " + format1.format(to.getTime()));

        //Stock google = YahooFinance.get("GOOG", from, to, Interval.DAILY);
        //List<HistoricalQuote> gh = google.getHistory();
        //test multiple historic data
        String[] ticks = {"AAPL", "BAC", "GOOG", "TSLA"};
        Map<String,Stock> mhd = YahooFinance.get(ticks,from,to,Interval.DAILY);



    }

    public HashMap<String,BigDecimal> getStocksPrice() throws IOException{
        HashMap<String, BigDecimal> prices = new HashMap<String, BigDecimal>();
        Map<String,Stock> data = YahooFinance.get(this.tickers);

        for (String s : data.keySet()){
            prices.put(s,data.get(s).getQuote(true).getPrice());
        }

        return prices;
    }

    public HashMap<String,List<HistoricalQuote>> getHistoricalQuote(Calendar from, Calendar to,Interval interval) throws IOException{
        Map<String,Stock> s = YahooFinance.get(this.tickers,from,to,interval);
        HashMap<String, List<HistoricalQuote>> historicData = new HashMap<String, List<HistoricalQuote>>();

        for (String h : s.keySet()){
            historicData.put(h,s.get(h).getHistory());
        }
        return historicData;
    }


}
