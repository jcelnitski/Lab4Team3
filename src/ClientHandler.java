
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import java.io.FileWriter;  
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/* 
Project: Lab 4
Purpose Details: GET/POST server
Course: IST 411
Author: Team 3
Date Developed: 9 June 2019
Last Date Changed: 9 June 2019
Revision: Fixed socket errors
*/

public class ClientHandler implements Runnable {
    
    private final Socket socket;
    
    public ClientHandler(Socket socket) {
        
        this.socket = socket;
        
    }
    
    @Override
    public void run() {
        
        System.out.println("\nClientHandler Started for " + this.socket);
        handleRequest(this.socket);
        System.out.println("ClientHandler Terminated for " + this.socket + "\n");
        
    }
    
    public void handleRequest(Socket socket) {
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            
            String headerLine = in.readLine();
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            
            if (httpMethod.equals("GET")) {
                
                System.out.println("Get method processed");
                StringBuilder contentBuilder = new StringBuilder();
                try (Stream<String> stream = Files.lines( Paths.get("/Users/leon/diary.txt"), StandardCharsets.UTF_8))
                {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                }
                System.out.println(contentBuilder);
                sendResponse(socket, 200, contentBuilder.toString());
                
            } 
            else if (httpMethod.equals("POST")) {
                System.out.println("Post method processed");
                String payload = in.readLine();
                System.out.println("Received payload: " + payload);
                try (FileWriter fw = new FileWriter("/Users/leon/diary.txt", true)) {
                    fw.append(payload);
                }
                sendResponse(socket, 200, "Entry received!");
            }
            
            else {
                
                System.out.println("The HTTP method is not recognized");
                sendResponse(socket, 405, "Method Not Allowed");
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            
        }
    }
    
    public void sendResponse(Socket socket, int statusCode, String responseString) {
        
        String statusLine;
        String serverHeader = "Server: WebServer\r\n";
        String contentTypeHeader = "Content-Type: text/html\r\n";
        
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {
            
            if (statusCode == 200) {
                
                statusLine = "HTTP/1.0 200 OK" + "\r\n";
                String contentLengthHeader = "Content-Length: "
                + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
                
            } else if (statusCode == 405) {
                
                statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
                
            } else {
                
                statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
                
            }
            
            out.close();
            
        } catch (IOException ex) {
            
        // Handle exception
            System.out.println("ERROR "+ex);
            ex.printStackTrace();
        }
    }
}