
import java.io.BufferedReader;
import static java.io.FileDescriptor.out;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import static sun.misc.MessageUtils.out;

public class HTTPClient {
    
    public HTTPClient() {
        
        System.out.println("HTTP Client Started");
        createRequest("GET");
        createRequest("POST");
        createRequest("GET");
    }
    
    private void createRequest(String type) {
        try {
            
            InetAddress serverInetAddress = InetAddress.getByName("127.0.0.1");
            Socket connection = new Socket(serverInetAddress, 80);
            
            try (OutputStream out = connection.getOutputStream(); 
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                System.out.println("SENDING " + type + " REQUEST");
                if (type.equals("GET")) {
                    sendGet(out);
                }
                if (type.equals("POST")) {
                    sendPost(out);
                }
                System.out.println(getResponse(in));
            } catch(SocketException ex) {
                ex.printStackTrace();
            } finally {
                connection.close();
            }
            
        } catch (IOException ex) {
            
            ex.printStackTrace();
        }
    }
    
    private void sendGet(OutputStream out) {
        try {
            out.write("GET /default\r\n".getBytes());
            out.write("User-Agent: Mozilla/5.0\r\n".getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void sendPost(OutputStream out)
    {
        try{
                out.write("POST /default\r\n".getBytes());
                out.write("Hello! This new entry was created by HTTPClient\r\n".getBytes());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String getResponse(BufferedReader in) {
        try {
            
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            
            return response.toString();
            
        } catch (IOException ex) {
            
            ex.printStackTrace();
            
        }
        
        return "";
        
    }
    
    public static void main(String[] args) {
        
        new HTTPClient();
    }
}