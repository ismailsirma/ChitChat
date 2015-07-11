package com.sirma.chat;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Created by ismailsirma on 03.7.2015.
 */
public class ChitChatClient extends JFrame{

    // initialize GUI variables
    // create a text field for user to enter their name
    private  JTextField usernameTF = new JTextField();
    // create a text field for user to enter their message
    private JTextField messageTF = new JTextField();
    private JLabel label1 = new JLabel();
    private JLabel statusbar = new JLabel();
    private JButton button1 = new JButton("Set nickname");
    private JButton button2 = new JButton("Send");

    private JTextPane textArea = new JTextPane();
    private JScrollPane jScrollPane1 = new JScrollPane(textArea);
    private Style def = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
    private StyledDocument document = textArea.getStyledDocument();

    // Generate a random color for the message text
    int redValue = 0 + (int)(Math.random()*255);
    int greenValue = 0 + (int)(Math.random()*255);
    int blueValue = 0 + (int)(Math.random()*255);
    private Color messageColor = new Color(redValue,greenValue,blueValue);

    private String usernamee;
    Date date = new Date();

    private ChitChatClientSwingWorker swingWorker;


    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChitChatClient chitchat = new ChitChatClient();
                chitchat.SetLayout();
                chitchat.setVisible(true);
            }
        });
    }

    public void messageReceived(String message) {

        // if current user send messages it has regular style, if not it will be italic
        if(message.startsWith("MESSAGE " + usernamee)) {
            updateMessageArea(message, "regular");
        } else{
            updateMessageArea(message, "italic");
        }

    }

    public void SetLayout(){

        setTitle("Chat Client");
        setSize(500, 400);
        // How frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the TextPane that shows incoming and outgoing messages
        createTextPane();

        jScrollPane1.setViewportView(textArea);

        label1.setText("Enter your username:");

        // scrollbar automatically goes down
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


    //************************ CreateLayout()  *******************************************************************//
        // Creating layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

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
        //is less than the minim um size as specified by the previous call to the setMinimumSize method.
        pack();

        //********************** Action Listeners ******************************************//

        // call swingWorker
        swingWorker = new ChitChatClientSwingWorker(textArea,document,
                usernamee);
        swingWorker.execute();
        // when user hits enter key in username text field, show it on status bar
        usernameTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernamee = usernameTF.getText();
                // send username to the server
                try {
                    // set the username in swingworker class
                    swingWorker.setUsernamee(usernamee);
                    swingWorker.sendMessage(usernamee);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                statusbar.setText("Username set!");
                usernameTF.setEnabled(false);
                //frame.setVisible(true);
            }
        });

        // when an action is performed, capture the action
        // button1 is the button sets the username
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernamee = usernameTF.getText();
                // send username to the server
                try {
                    // set the username in swingworker class
                    swingWorker.setUsernamee(usernamee);
                    swingWorker.sendMessage(usernamee);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                statusbar.setText("Username set!");
                usernameTF.setEnabled(false);
                //frame.setVisible(true);
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
                try {
                    swingWorker.sendMessage(messageTF.getText());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                //clear the message area
                messageTF.setText("");
                //clear the status area
                statusbar.setText("");
                usernameTF.setEnabled(false);
                //frame.setVisible(true);
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
                // send the message to the server
                try {
                    swingWorker.sendMessage(messageTF.getText());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                //clear the message area
                messageTF.setText("");
                //clear the status area
                date = new Date();
                statusbar.setText("Your message at " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + " has been sent");
                usernameTF.setEnabled(false);
                //frame.setVisible(true);
            }
        });
    }

    private JTextPane createTextPane(){

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

    private void updateMessageArea(String serverMessage,String stylename){
        try {
            //document = createTextPane().getStyledDocument();
            Style style = textArea.getStyle(stylename);

            document.insertString(document.getLength(), serverMessage + "\n", style);
            //frame.setVisible(true);
            setVisible(true);
        }catch(BadLocationException e){
            System.out.println(e);
        }
    }

}

