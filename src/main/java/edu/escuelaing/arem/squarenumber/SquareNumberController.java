package edu.escuelaing.arem.squarenumber;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
@RestController
@RequestMapping(value = "/square")
public class SquareNumberController {

    public static final String SERVER_ADDRESS = "https://arem-proyecto2.herokuapp.com/";
    public static final String SQUARE_GET_PARAM = "square";
    
    @GetMapping
    public @ResponseBody
    String squareNumber(@RequestParam("value") long value) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.getForEntity(SERVER_ADDRESS + SQUARE_GET_PARAM + "?value=" + value, String.class);
        
        return response.getBody();
    }
}
