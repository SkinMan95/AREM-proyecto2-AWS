package edu.escuelaing.arem;

import edu.escuelaing.arem.requesters.Requester;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public class HttpServer {

    public static final int DEFAULT_PORT = 8080;
    public static boolean finished = false;
    public static int port = DEFAULT_PORT;
    public static final String WEB_FOLDER = "./web_files/";

    public static void main(String[] args) throws Exception {
        try {
            port = new Integer(System.getenv("PORT"));
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex + " using default address (" + DEFAULT_PORT + ")");
            port = DEFAULT_PORT;
        }

        filesCache = new HashMap<>();
        generateResponders();

        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(port);

        System.out.println("Ready to receive through " + port + "...");

        Socket clientSocket = null;
        while (!finished) {
            clientSocket = serverSocket.accept();

            (new ResponderThread(clientSocket)).start();
        }

        serverSocket.close();
    }

    public static Map<String, byte[]> filesCache;
    public static Map<String, Responder> respondersMap;

    private static byte[] getDataFromFile(String fileName) {
        if (filesCache.containsKey(fileName)) {
            return filesCache.get(fileName);
        }

        File f = new File(WEB_FOLDER + fileName);
        byte[] data = new byte[(int) f.length()];
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(f);
            inputStream.read(data);
        } catch (IOException ex) {
            System.err.println("Error getting file: " + ex);
        }

        filesCache.put(fileName, data);
        return data;
    }

    private static void generateResponders() {
        respondersMap = new HashMap<>();
        respondersMap.put("/", (out) -> {
            byte[] data = getDataFromFile("index.html");

            try {
                DataOutputStream binaryOut = new DataOutputStream(out);
                binaryOut.writeBytes("HTTP/1.1 200 OK\r\n");
                binaryOut.writeBytes("Content-Type: text/html; charset=utf-8\r\n");
                binaryOut.writeBytes("Content-Encoding: raw\r\n");
                binaryOut.writeBytes("Content-Length: " + data.length);
                binaryOut.writeBytes("\r\n\r\n");
                binaryOut.write(data);

                binaryOut.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        respondersMap.put("/css/css.css", (out) -> {
            byte[] data = getDataFromFile("css/css.css");

            try {
                DataOutputStream binaryOut = new DataOutputStream(out);
                binaryOut.writeBytes("HTTP/1.1 200 OK\r\n");
                binaryOut.writeBytes("Content-Type: text/css; charset=utf-8\r\n");
                binaryOut.writeBytes("Content-Encoding: raw\r\n");
                binaryOut.writeBytes("Content-Length: " + data.length);
                binaryOut.writeBytes("\r\n\r\n");
                binaryOut.write(data);

                binaryOut.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        respondersMap.put("/js/SquareController.js", (out) -> {
            byte[] data = getDataFromFile("/js/SquareController.js");

            try {
                DataOutputStream binaryOut = new DataOutputStream(out);
                binaryOut.writeBytes("HTTP/1.1 200 OK\r\n");
                binaryOut.writeBytes("Content-Type: text/javascript; charset=utf-8\r\n");
                binaryOut.writeBytes("Content-Encoding: raw\r\n");
                binaryOut.writeBytes("Content-Length: " + data.length);
                binaryOut.writeBytes("\r\n\r\n");
                binaryOut.write(data);

                binaryOut.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static final String getRegex = "[^=]*[=](\\d+)";

    public static void processPetition(String petition, OutputStream out) {
        StringTokenizer strtok = new StringTokenizer(petition, " ");
        if (strtok.hasMoreElements() && ((String) strtok.nextElement()).equals("GET")) {
            System.out.println("GET petition recognized: " + petition);

            String resourceName = "";
            if (strtok.hasMoreElements()) {
                resourceName = ((String) strtok.nextElement());
                if (respondersMap.containsKey(resourceName)) {
                    System.out.println("GET petition is valid");
                    respondersMap.get(resourceName).respond(out); // responds petition
                } else if (resourceName.contains(".do")) {
                    System.out.println(resourceName);
                    // it's an action
                    Pattern p = Pattern.compile(getRegex);
                    if (resourceName.contains(".do")) {
                        String className = resourceName.substring(1, resourceName.indexOf(".do"));
                        String arguments[] = resourceName.substring(resourceName.contains("?") ? resourceName.indexOf("?") + 1 : 0).split("&");

                        System.out.println(Arrays.toString(arguments));

                        for (int i = 0; i < arguments.length; i++) {
                            // System.out.println(arguments[i]);
                            Matcher m = p.matcher(arguments[i]);
                            if (m.find()) {
                                // System.out.println(m.group(0));
                                System.out.println(m.group(1));
                                arguments[i] = m.group(1);
                            } else {
                                System.err.println("Not a valid expression: \"" + arguments[i] + "\"");
                                arguments[i] = "";
                            }
                        }

                        loadAndExecuteRequest(className, arguments, out);
                    }
                }
            }
        }
    }

    private static Requester getRequesterByName(String name) {
        Requester r = null;
        try {
            String convertedName = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase() + "Requester"; // Capitalize as NameRequester
            Object p = Class.forName("edu.escuelaing.arem.requesters." + convertedName).newInstance();
            r = (Requester) p;
        } catch (Exception ex) {
            System.err.println("Error generating Requester \"" + name + "\": " + ex);
        }
        
        return r;
    }

    public static void loadAndExecuteRequest(String className, String[] arguments, OutputStream out) {
        PrintWriter o = new PrintWriter(out, true);
        Requester r = getRequesterByName(className);
        if (r != null) {
            o.println("HTTP/1.1 200 \r\n"
                    + "Content-Type: raw ;charset=UTF-8\r\n"
                    + "\r\n"
                    + r.request(arguments));
        } else {
            o.println("HTTP/1.1 400 \r\n"
                    + "\r\n");
        }
        o.close();
    }

}
