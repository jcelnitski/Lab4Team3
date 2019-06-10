
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* 
Project: Lab 4
Purpose Details: GET/POST server
Course: IST 411
Author: Team 3
Date Developed: 9 June 2019
Last Date Changed: 9 June 2019
Revision: Fixed socket errors
*/

public class WebServer {
    public WebServer() {
        System.out.println("Webserver Started");
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            while (true) {
                System.out.println("Waiting for client request");
                Socket remote = serverSocket.accept();
                System.out.println("Connection made");
                new Thread(new ClientHandler(remote)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String args[]) {
        new WebServer();
    }
}