package co.edu.eci.ParcialMathService.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class MathService {

    private List<Integer> lucasSeq;

    public List<Integer> getLucasSeq(int n){
        ArrayList lucasSeq = new ArrayList<>();
        for(int i = 0; i < n; i ++) {
            lucasSeq.add(calculateLucas(i));
        }

        return lucasSeq;
    }

    public Integer calculateLucas(int n){
        if(n <= 0){
            return 2;
        }
        if(n == 1){
            return 1;
        }

        int result = 0;
        result = calculateLucas(n - 1) + calculateLucas(n - 2);
        return result;

    }
}
