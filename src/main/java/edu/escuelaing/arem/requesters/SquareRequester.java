package edu.escuelaing.arem.requesters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public class SquareRequester implements Requester {

    public static final String SERVER_URL = "https://arem-proyecto2.herokuapp.com/";

    @Override
    public String request(String[] arguments) {
        if (arguments.length < 1) {
            return "0";
        }

        HttpURLConnection con = null;

        String result = "";

        try {

            URL myurl = new URL(SERVER_URL + "square?value=" + arguments[0]);
            con = (HttpURLConnection) myurl.openConnection();

            con.setRequestMethod("GET");
            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());
            result = content.toString();
        } catch (Exception ex) {
            System.err.println("Error connecting to remote server: " + ex);
        }

        if (con != null) {
            con.disconnect();
        }

        String[] res = result.split("\\R+");
        
        System.out.println("RESULT: " + res[res.length - 1]);

        return res[res.length - 1];
    }

}
