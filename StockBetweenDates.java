import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class StockBetweenDates {

    public static final String D_MMM_YYYY = "d-MMM-yyyy";
    public static final String URL = "https://jsonmock.hackerrank.com/api/stocks/?page=";
    public static final String GET = "GET";

    static void openAndClosePrices(String firstDate, String lastDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(D_MMM_YYYY, new Locale("en"));
            Date firstDate_ = simpleDateFormat.parse(firstDate);
            Date lastDate_ = simpleDateFormat.parse(lastDate);
//            if (firstDate_.before(simpleDateFormat.parse("5-January-2000"))
//                    || lastDate_.after(simpleDateFormat.parse("1-January-2014"))) {
//                //do nothing as this comparisson it's not required;
//            }
            List<Stock> filteredStocks = new ArrayList<>();
            for (int i = 1; i > -1; i++) {
                URL url = new URL(URL + i);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(GET);
                Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Root response = gson.fromJson(reader, Root.class);
                if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                    try {
                        response.getData().sort(
                                Comparator.comparing(stock -> {
                                    try {
                                        return simpleDateFormat.parse(stock.getDate());
                                    } catch (ParseException e) {
                                        System.out.println("Error processing the Date in stocks REST call: " + e);
                                    }
                                    return null;
                                }));
                        String firstDateSorted = response.getData().get(0).getDate();
                        String lastDateSorted = response.getData().get(response.getData().size() - 1).getDate();
                        long firstYearSorted = Long.parseLong(firstDateSorted.substring(firstDateSorted.length() - 4));
                        long lastYearSorted = Long.parseLong(lastDateSorted.substring(lastDateSorted.length() - 4));
                        long firstYearSubmitted = Long.parseLong(firstDate.substring(firstDate.length() - 4));
                        long lastYearSubmitted = Long.parseLong(lastDate.substring(lastDate.length() - 4));
                        if (lastYearSorted < firstYearSubmitted || simpleDateFormat.parse(lastDateSorted).before(firstDate_)) {
                           continue;
                        }
                        if (firstYearSorted > lastYearSubmitted || simpleDateFormat.parse(firstDateSorted).after(lastDate_)) {
                            i = -2;
                            break;
                        }
                        filteredStocks.addAll(response.getData().stream().filter(t -> {
                                    Date date = null;
                                    try {
                                        date = simpleDateFormat.parse(t.getDate());
                                    } catch (ParseException e) {
                                        System.out.println("Error processing the Date in stocks REST call: " + e);
                                    }
                            assert date != null;
                            return date.after(firstDate_) && date.before(lastDate_);
                                }
                        ).collect(Collectors.toList()));
                    } catch (Exception e) {
                        System.out.println("Error sorting the stocks REST call: " + e);
                    }
                    if (simpleDateFormat.parse(response.getData().get(response.getData().size() - 1).getDate()).after(lastDate_)) {
                        break;
                    }
                }
            }
            filteredStocks.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error processing the stocks REST call: " + e);
        } catch (ParseException ex) {
            System.out.println("Error processing the Date in stocks REST call: " + ex);
        }

    }

    public static class Stock {

        private final static String SPACE = " ";

        private String date;
        private float open;
        private float close;
        private float high;
        private float low;

        public String getDate() {
            return this.date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public float getOpen() {
            return this.open;
        }

        public void setOpen(float open) {
            this.open = open;
        }

        public float getClose() {
            return this.close;
        }

        public void setClose(float close) {
            this.close = close;
        }

        public float getHigh() {
            return this.high;
        }

        public void setHigh(float high) {
            this.high = high;
        }

        public float getLow() {
            return this.low;
        }

        public void setLow(float low) {
            this.low = low;
        }

        @Override
        public String toString() {
            return this.date + SPACE + this.open + SPACE + this.close;
        }

    }

    public static class Root {
        private int page;
        private int per_page;
        private int total;
        private int total_pages;
        private List<Stock> data;

        public int getPage() {
            return this.page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPer_page() {
            return this.per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTotal() {
            return this.total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal_pages() {
            return this.total_pages;
        }

        public void setTotal_pages(int total_pages) {
            this.total_pages = total_pages;
        }

        public List<Stock> getData() {
            return this.data;
        }

        public void setData(List<Stock> data) {
            this.data = data;
        }

    }

    public static void main(String[] args) throws ParseException {
        String _firstDate = args[0];
        String _lastDate = args[1];
        openAndClosePrices(_firstDate, _lastDate);
    }
}

