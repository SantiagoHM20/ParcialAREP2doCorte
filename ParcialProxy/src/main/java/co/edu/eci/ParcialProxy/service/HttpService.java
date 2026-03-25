package co.edu.eci.ParcialProxy.service;

import co.edu.eci.ParcialProxy.dto.LucasResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HttpService {

    private String[] services = {
            System.getenv("SERVICE1"),
            System.getenv("SERVICE2")
    };

    private RestTemplate restTemplate = new RestTemplate();
    private int counter = 0;

    public synchronized List<Long> getResult(String value) {
        String serviceUrl = services[counter];
        counter = (counter + 1) % services.length;

        String url = serviceUrl + "/lucasseq?value=" + value;
        LucasResponse response = restTemplate.getForObject(url, LucasResponse.class);

        return Arrays.stream(response.getOutput().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}



