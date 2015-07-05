package com.sirma.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created by ismailsirma on 23.6.2015.
 * ChitChatServer with threaded functionality for multi-user
 */
public class ChitChatServer{

    private static final int port = 10002;

    private static HashSet<String> usernames = new HashSet<String>();

    private static HashSet<PrintWriter> printwriters = new HashSet<PrintWriter>();


    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            // instantiating a server socket object assigned to port
            serverSocket = new ServerSocket(port);

            // accept method of server class being invoked.
            // accept method waits until a client connects to the server with given port
            // this socket is created in order to receive incoming messages
            try {
                while (true){
                new ChitChat(serverSocket.accept()).start();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Ooops! There was a problem creating Server Socket!");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("There was a problem when closing the connection.");
            }
        }
    }


    private static class ChitChat extends Thread {

        private String username;
        private Socket clientSocket;
        private BufferedReader input;
        private PrintWriter output;

        public ChitChat(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // Clients output object for sending outputs
                output = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                // Creating a buffer Reader which gets the input stream of the client socket
                // BufferedReader() -> InputStreamReader() -> getInputStream() -> clientSocket
                input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));


                while (true) {
                    output.println("Please submit your name here:");
                    username = input.readLine();
                    if(username == null){
                        continue;
                    }
                    synchronized (usernames){
                        if(!usernames.contains(username)){
                            usernames.add(username);
                            System.out.println("User named:" + username + " ,added.");
                            break;
                        }
                    }
                }
                output.println("Your name has been confirmed.");
                printwriters.add(output);
                //System.out.println("Print Writer named:" + output + " ,added.");

                while(true){
                    String userInput = input.readLine();
                    if((userInput == null) || (userInput == "")){
                        break;
                    }
                    for (PrintWriter writer: printwriters){
                        writer.println("MESSAGE " + username + ": " + userInput);
                        //System.out.println("Sending message to " + username + " via " + writer);
                    }
                }

            } catch (IOException e) {
                System.out.println(e);
            } finally{
                System.out.println("The user: " + username + " has been logged out :(");
                if(username !=  null){
                    usernames.remove(username);
                }
                if(output != null){
                    printwriters.remove(output);
                }
                try{
                    clientSocket.close();
                } catch(IOException e){

                }
            }
        }


    }
}
