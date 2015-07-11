package com.sirma.chat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by reloaded on 6.7.2015.
 */
public class ChitChatClientSwingWorker extends SwingWorker<Integer,String> {

    PrintWriter output;
    BufferedReader input;
    String usernamee;
    JTextPane textPane;
    StyledDocument document;
    String serverMessage;
    Socket socket;

    public ChitChatClientSwingWorker(JTextPane textArea, StyledDocument document,
                                     String username){
        this.usernamee = username;
        this.textPane = textArea;
        this.document = document;
        try {
            // while server is waiting for a call,
            // client instantiates socket object with specified server address and port
            socket = new Socket("localhost", 10002);
            // client gets the server's output stream
            output = new PrintWriter(socket.getOutputStream(),true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUsernamee(String name){
        this.usernamee = name;
    }

    protected void sendMessage(String message) throws Exception{
            output.println(message);
    }

    private void updateMessageArea(String serverMessage,String stylename){
        try {
            //document = createTextPane().getStyledDocument();
            Style style = textPane.getStyle(stylename);

            document.insertString(document.getLength(), serverMessage + "\n", style);

        }catch(BadLocationException e){
            System.out.println(e);
        }
    }

    public void messageReceived(String message) {

        // if current user send message, the message has regular style, if not it will be italic
        if(message.startsWith("MESSAGE " + usernamee)) {
            updateMessageArea(message, "regular");
        } else{
            updateMessageArea(message, "italic");
        }

    }

    @Override
    protected Integer doInBackground() throws Exception{

        try {
            // Create a buffered reader to the socket
            // in order to get the input from the server
            // Buffered reader reads the bytes and converts them into chars.
            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));


            while(true){
                // receive client input
                serverMessage = input.readLine();

                // write the message to the Text Pane
                messageReceived(serverMessage);
                System.out.println(serverMessage);
                // end loop
                if (serverMessage.equals("Bye.")) {
                    break;
                }

            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("There is an I/O problem or Connection has been reset");
        }

        return 1;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        // Messages received from the doInBackground() (when invoking the publish() method)
        messageReceived(serverMessage);
    }

}

