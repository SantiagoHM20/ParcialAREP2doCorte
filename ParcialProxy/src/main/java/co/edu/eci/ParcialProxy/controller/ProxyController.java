package co.edu.eci.ParcialProxy.controller;

import co.edu.eci.ParcialProxy.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ProxyController {

    private HttpService httpService;
    @Autowired
    public ProxyController(HttpService httpService){
        this.httpService = httpService;
    }

    @GetMapping("/lucasseq")
    public ResponseEntity<?> getLucasSeq(@RequestParam("value") String value){
        List<?> lucasSeq = httpService.getResult(value);

        String output = lucasSeq.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Secuencia de Lucas");
        response.put("input", value);
        response.put("output", output);

        return ResponseEntity.ok(response);
    }
}

