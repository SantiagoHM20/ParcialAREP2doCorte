package co.edu.eci.ParcialProxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LucasResponse {
    private String operation;
    private int input;
    private String output;
}

