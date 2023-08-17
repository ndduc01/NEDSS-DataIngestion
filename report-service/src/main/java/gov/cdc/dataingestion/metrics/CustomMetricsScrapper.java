package gov.cdc.dataingestion.metrics;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


// THIS CLASS NEEDS TO BE RUN EVERY 15 SECS TO SCRAP THE METRICS AND SEND IT TO THE DATABASE
public class CustomMetricsScrapper {

    public static String getMetricsFromPrometheus() throws IOException {
        System.out.println("Printing inside function");
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            //HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:9000/actuator/prometheus/logback_events_total")).build();
            //HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:9090/api/v1/query?query=logback_events_total")).build();
            //HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://10.52.1.180:9090/api/v1/query?query=logback_events_total")).build();
            //HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/actuator/prometheus/logback_events_total")).build();

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:9090/api/v1/query?query=custom_requests_total")).build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            //System.out.println("Response body is..." + response.getMediaType());
            String responseBody = response.body().toString();

            System.out.println("Response body is..." + responseBody);

            final JSONObject obj = new JSONObject(responseBody);
            final JSONObject data = obj.getJSONObject("data");
            final JSONArray metric1 = data.getJSONArray("result");
            for(int i = 0; i < metric1.length(); i++) {
                JSONObject jsonString = metric1.getJSONObject(i);
                JSONObject metricVal = jsonString.getJSONObject("metric");
                System.out.println(metricVal.getString("__name__"));
                JSONArray jsonString1 = jsonString.getJSONArray("value");
                System.out.println(jsonString1.get(1));
            }
            return responseBody;
        } catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }
        return"Empty";
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Printing inside main");
        System.out.println(getMetricsFromPrometheus());

    }
}
