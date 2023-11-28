package ru.didcvee.springmicro.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.didcvee.springmicro.client.HttpCurrencyDateRateClient;
import ru.didcvee.springmicro.schema.ValCurs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CBRFService {
    private final Cache<LocalDate, Map<String,BigDecimal>> cache;

    private final HttpCurrencyDateRateClient client;

    @Autowired
    public CBRFService(HttpCurrencyDateRateClient client) {
        this.client = client;
        this.cache = CacheBuilder.newBuilder().build();
    }

    public BigDecimal requestByCurrencyCode(String code){
        try {
            return cache.get(LocalDate.now(),this::callAllByCurrentDate).get(code);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, BigDecimal> callAllByCurrentDate() {
        var xml = client.requestByDate(LocalDate.now());
        ValCurs response = unmarshall(xml);
        return response.getValute().stream().collect()
    }

    private ValCurs unmarshall(String xml){
        try (StringReader reader = new StringReader(xml)){
            JAXBContext context = JAXBContext.newInstance(ValCurs.class);
            return (ValCurs) context.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e){
            throw new RuntimeException(e);
        }
    }

}
