package com.chat;
import javax.swing.BoxLayout;import javax.swing.JFrame;import javax.swing.JLabel;import javax.swing.JList;import javax.swing.JPanel;import javax.swing.JScrollPane;import javax.swing.ListSelectionModel;import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Override;import java.lang.Runnable;import java.lang.String;import java.lang.System;import java.lang.Thread;import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;


public class MyChatClient {

   JFrame frame;
   JPanel panel;

    JLabel  usersLabel;
     JList userList;
    Vector<String> users = new Vector<String>();
    Socket socket;
    PrintWriter writer;
    private InputStreamReader inputStream;
    private BufferedReader reader;
    String name;
    String selectedUserToChat;
    HashMap<String , MyJFrame> chatDialogBoxs = new HashMap<String, MyJFrame>();

    public MyChatClient(String name) {

        this.name = name;
    }

    public static void main(String[] args) {
        MyChatClient client = new MyChatClient("Shantala");
        client.go();
    }

    public void go() {
        setUpGui();
        try {
            socket = new Socket("127.0.0.1" , 4242);
            inputStream = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStream);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Sending registration message for " + name);
            writer.println("Register:" + name);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageReceiver messageReceiver = new MessageReceiver();
        Thread newThread = new Thread(messageReceiver);
        newThread.start();
    }

    private void setUpGui() {
        frame = new JFrame(" MyClient : " + name);
        panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        usersLabel = new JLabel("Users");

        userList = new JList();
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(userList);
        userList.setListData(users);

        panel.add(usersLabel);
        panel.add(theList);
        userList.addListSelectionListener(new MyListSelectionListener());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER , panel);
        frame.setSize(800 , 800);
        frame.pack();
        frame.setVisible(true);
    }



    private class MessageReceiver implements Runnable{
        @java.lang.Override
        public void run() {
            String message;
            String[] usersMessage;
            try {
                while ((message = reader.readLine()) != null) {

                    System.out.println("Read message from server " + message);
                    if(message.contains("Users")){
                        users.clear();
                        usersMessage = message.split(":");
                        for (int i = 1; i < usersMessage.length; i++) {
                            String user = usersMessage[i];
                            if(!user.equals(name)) users.add(user);
                        }
                        userList.setListData(users);
                    } else{
                        MyJFrame currFrame;
                        usersMessage = message.split(":");
                        System.out.println("A normal message , about to append");
                        if(chatDialogBoxs.containsKey(usersMessage[0])) {
                            System.out.println("Already have a chat dialog box open for " + usersMessage[0]);
                            currFrame = chatDialogBoxs.get(usersMessage[0]);
                            if(!currFrame.isVisible()) currFrame.setVisible(true);
                        }
                        else
                        {
                            System.out.println("Opening a new chat dialog box open for " + usersMessage[0]);
                            currFrame = new MyJFrame(name , usersMessage[0] , writer);
                            chatDialogBoxs.put(usersMessage[0] , currFrame);
                        }

                        currFrame.appendChatMessage(message + "\n");
                    }
                }
            }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    private class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            MyJFrame currFrame;
            if (!listSelectionEvent.getValueIsAdjusting()) {
                String selected = (String) userList.getSelectedValue();
                if (selected != null) {

                    selectedUserToChat = selected;
                    if(chatDialogBoxs.containsKey(selectedUserToChat)){
                        System.out.println("Open a dialog box to chat with " + selected);
                         currFrame = chatDialogBoxs.get(selectedUserToChat);
                        currFrame.setSize(800 , 800);
                        currFrame.pack();
                        currFrame.setVisible(true);
                         //just open the frame if its not already open
                    }
                    else
                    {
                        System.out.println("Create a new dialog box to chat with " + selected);
                        //create a new one and make it visible
                        currFrame = createADialogFrame(name ,selected , writer);
                        chatDialogBoxs.put(selectedUserToChat , currFrame);
                    }
                }

            }
        }
    }

    private MyJFrame createADialogFrame(String name, String toChat, PrintWriter writer) {

        return new MyJFrame(name ,toChat , writer);
    }
}

