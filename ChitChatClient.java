package com.sirma.chat;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * Created by ismailsirma on 03.7.2015.
 */
public class ChitChatClient {

    PrintWriter output;
    BufferedReader input;

    // initialize GUI variables
    JTextField usernameTF;
    JTextField messageTF;
    JLabel label1;
    JScrollPane jScrollPane1;
    JLabel statusbar;
    JButton button1;
    JButton button2;

    private JTextPane textArea = new JTextPane();
    StyledDocument document;
    Style def;

    JFrame frame;
    String usernamee = null;
    Date date = new Date();

    // Generate a random color for the message text
    int redValue = 0 + (int)(Math.random()*255);
    int greenValue = 0 + (int)(Math.random()*255);
    int blueValue = 0 + (int)(Math.random()*255);
    final Color messageColor = new Color(redValue,greenValue,blueValue);


    public static void main(String[] args){
        ChitChatClient ch = new ChitChatClient();
        ch.run();
    }

    public ChitChatClient() {

    }

    private void run(){
        try {

            // Set the graphical user interface
            SetLayout();

            // set the frame visible
            frame.setVisible(true);

            // while server is waiting for a call,
            // client instantiates socket object with specified server address and port
            Socket socket = new Socket("localhost", 10002);
            // client gets the server's output stream
            output = new PrintWriter(socket.getOutputStream(),true);

            // Create a buffered reader to the socket
            // in order to get the input from the server
            // Buffered reader reads the bytes and converts them into chars.
            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String serverMessage;


            // when user hits enter key in username text field, show it on status bar
            usernameTF.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    usernamee = usernameTF.getText();
                    output.println(usernamee);
                    statusbar.setText("Username set!");
                    usernameTF.setEnabled(false);
                    frame.setVisible(true);
                }
            });

            // when an action is performed, capture the action
            // button1 is the button sets the username
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    usernamee = usernameTF.getText();
                    output.println(usernamee);
                    statusbar.setText("Username set!");
                    usernameTF.setEnabled(false);
                    frame.setVisible(true);
                }
            });


            // when user writes down a message and hits the enter key, send the message
            messageTF.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (usernamee == null) {
                        messageTF.setText("");
                        return;
                    }
                    // send the message to the server
                    output.println(messageTF.getText());
                    //clear the message area
                    messageTF.setText("");
		    // Show the time that message has been sent in the statusbar
                    date = new Date();
                    statusbar.setText("Your message at " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + " has been sent");
                    usernameTF.setEnabled(false);
                    frame.setVisible(true);
                }
            });

            // button2 is the button sends the message
            button2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // take the text and put it on the label

                    // if user didn't specify the user name
                    if(usernamee == null){
                        messageTF.setText("");
                        return;
                    }

                    output.println(messageTF.getText());
                    //clear the message area
                    messageTF.setText("");
		    // Show the time that message has been sent in the statusbar
                    date = new Date();
                    statusbar.setText("Your message at " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + " has been sent");
                    usernameTF.setEnabled(false);
                    frame.setVisible(true);
                }
            });


            while(true){

                try {
                    // receive client input
                    serverMessage = input.readLine();
                    // JTextArea method
                    //textArea.append(serverMessage + "\n");
                    // write the message to the Text Pane
                    if(serverMessage.startsWith("MESSAGE " + usernamee)) {

                        document = createTextPane().getStyledDocument();
                        Style style1 = createTextPane().getStyle("regular");

                        document.insertString(document.getLength(), serverMessage + "\n", style1);
                        frame.setVisible(true);
                    } else{
                        document = createTextPane().getStyledDocument();
                        Style style2 = createTextPane().getStyle("italic");

                        document.insertString(document.getLength(), serverMessage + "\n", style2);
                        frame.setVisible(true);
                    }
                    // end loop
                    if (serverMessage.equals("Bye.")) {
                        break;
                    }

                }catch(BadLocationException e){
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("There is an I/O problem or Connection has been reset");
        }

    }

    public void SetLayout(){

        // Create a frame
        frame = new JFrame("Chat Client");
        frame.setSize(500, 400);

        // create a text field for user to enter their name
        usernameTF = new JTextField();
        // create a text field for user to enter their message
        messageTF = new JTextField();

        button1 = new JButton("Set nickname");
        button2 = new JButton("Send");
        label1 = new JLabel();
        statusbar = new JLabel();

        // How frame is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Chat Client");

        // text area where incoming and outgoing messages being seen

        // If JTextArea was used instead of JTextPane
        //textArea.setColumns(20);
        //textArea.setRows(5);
        //textArea.setLineWrap(true);
        //textArea.setWrapStyleWord(true);
        //textArea.setEditable(false);

        // Create the TextPane that shows incoming and outgoing messages
        createTextPane();

        jScrollPane1 = new JScrollPane(textArea);
        jScrollPane1.setViewportView(textArea);

        label1.setText("Enter your username:");



        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Creating layout
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);

        //Create a parallel group for the horizontal axis
        GroupLayout.ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        //Create a sequential and a parallel groups
        GroupLayout.SequentialGroup h1 = layout.createSequentialGroup();
        GroupLayout.ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);

        //Add a container gap to the sequential group h1
        h1.addContainerGap();

        //Add a scroll pane and a label to the parallel group h2
        h2.addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE);
        h2.addComponent(statusbar, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE);

        //Create a sequential group h3
        GroupLayout.SequentialGroup h3 = layout.createSequentialGroup();
        h3.addComponent(label1, 0, GroupLayout.DEFAULT_SIZE, 150);
        h3.addComponent(usernameTF, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h3.addComponent(button1, 0, GroupLayout.DEFAULT_SIZE, 120); // set username button is added

        // new added
        GroupLayout.SequentialGroup h4 = layout.createSequentialGroup();
        h4.addComponent(messageTF, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        h4.addComponent(button2, 0, GroupLayout.DEFAULT_SIZE, 120);    // send message button is added

        hGroup.addGroup(h4);
        //Add the group h3 to the group h2
        hGroup.addGroup(h3);
        //Add the group h2 to the group h1
        hGroup.addGroup(h2);

        h1.addContainerGap();

        //Add the group h1 to the hGroup
        hGroup.addGroup(GroupLayout.Alignment.TRAILING, h1);
        //Create the horizontal group
        layout.setHorizontalGroup(hGroup);

        //Create a parallel group for the vertical axis
        GroupLayout.ParallelGroup vGroup = layout.createParallelGroup();
        //Create a sequential group v1
        GroupLayout.SequentialGroup v1 = layout.createSequentialGroup();
        //Add a container gap to the sequential group v1
        v1.addContainerGap();
        //Create a parallel group v3
        GroupLayout.ParallelGroup v3 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v3.addComponent(label1);
        v3.addComponent(usernameTF);
        v3.addComponent(button1);       // set username button is added
        //Create a parallel group v2
        GroupLayout.ParallelGroup v2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v2.addComponent(messageTF,0,70,300);
        v2.addComponent(button2);              // send message button is added
        //Add the group v2 tp the group v1
        v1.addGroup(v3);
        v1.addGroup(v2);
        v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        v1.addComponent(jScrollPane1, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        v1.addComponent(statusbar);
        v1.addContainerGap();

        //Add the group v1 to the group vGroup
        vGroup.addGroup(v1);
        //Create the vertical group
        layout.setVerticalGroup(vGroup);

        //Window to be sized to fit the preferred size and layouts of its sub components.
        //The resulting width and height of the window are automatically enlarged if either of dimensions
        //is less than the minimum size as specified by the previous call to the setMinimumSize method.
        frame.pack();
    }

    private JTextPane createTextPane(){

        document = textArea.getStyledDocument();

        def = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
        Style regular = document.addStyle( "regular", def );

        StyleConstants.setForeground(regular,messageColor);

        // Create an italic style
        Style italic = document.addStyle( "italic", regular );
        StyleConstants.setItalic( italic, true );
        StyleConstants.setForeground(italic, Color.BLUE);

        // Create a highlight style
        Style highlight = document.addStyle( "highlight", regular );
        StyleConstants.setBackground( highlight, Color.yellow );

        // add the scrollpane
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(100, 200));

        return textArea;
    }
}
