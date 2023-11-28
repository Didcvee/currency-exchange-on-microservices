package ru.didcvee.springmicro.client;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SBRFCurrencyRateClient implements HttpCurrencyDateRateClient{
    private static final String PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);
    @Override
    public String requestByDate(LocalDate date) {
        var baseUrl = "https://cbr.ru/scripts/XML_daily.asp";
        var client = HttpClient.newHttpClient();
        var url = buildUriRequest(baseUrl,date);
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    private String buildUriRequest(String baseUrl,LocalDate date) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("date-req",FORMATTER.format(date))
                .build()
                .toUriString();
    }
}
