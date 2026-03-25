package co.edu.eci.ParcialProxy.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class HttpService {

    private String service1 = System.getenv("SERVICE1");
    private String service2 = System.getenv("SERVICE2");

    private RestTemplate restTemplate = new RestTemplate();

    public List<Long> getResult(String value) {

        try {
            Long[] result = restTemplate.getForObject(
                    service1 + "/pell?value=" + value,
                    Long[].class
            );
            return Arrays.asList(result);

        } catch (Exception e) {

            Long[] result = restTemplate.getForObject(
                    service2 + "/pell?value=" + value,
                    Long[].class
            );
            return Arrays.asList(result);
        }
    }
}



