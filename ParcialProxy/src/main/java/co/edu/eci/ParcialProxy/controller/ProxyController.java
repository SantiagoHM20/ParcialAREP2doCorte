package co.edu.eci.ParcialProxy.controller;

import co.edu.eci.ParcialProxy.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

    private HttpService httpService;
    @Autowired
    public ProxyController(HttpService httpService){
        this.httpService = httpService;
    }

    @GetMapping("/lucasseq")
    public ResponseEntity<?> getLucasSeq(@RequestParam("value") String value){
        return ResponseEntity.ok(httpService.getResult(value));
    }
}

