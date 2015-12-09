package com.chat;import javax.swing.BoxLayout;import javax.swing.JFrame;import javax.swing.JLabel;import javax.swing.JPanel;import javax.swing.JTextArea;
import java.awt.BorderLayout;import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Override;import java.lang.Runnable;import java.lang.String;import java.lang.System;import java.lang.Thread;import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;import java.util.HashMap;import java.util.Iterator;import java.util.LinkedHashSet;import java.util.Set;

public class MyChatServer {


    JFrame frame;
    JPanel panel;
    JTextArea messageTextBox;
    JTextArea usersTextBox;
    JLabel messageLabel;
    JLabel usersLabel;
    ArrayList<PrintWriter> clientWriters;
    java.util.Set<String> users = new LinkedHashSet<String>();
    HashMap<String, PrintWriter> mapOfUsersAndCorrespondingWriter = new HashMap<String, PrintWriter>();

    public static void main(String[] args) {
        MyChatServer server = new MyChatServer();
        server.go();
    }

    private void go() {
        setUpGui();
        try {
            ServerSocket serverSocket = new ServerSocket(4242);
            clientWriters = new ArrayList<PrintWriter>();
            while(true){
                Socket socket = serverSocket.accept();
                MessageReceiver messageReceiver = new MessageReceiver(socket);
                Thread newThread = new Thread(messageReceiver);
                newThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpGui() {
        frame = new JFrame("Simple Server");
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        messageTextBox = new JTextArea();
        messageTextBox.setLineWrap(true);
        messageTextBox.setWrapStyleWord(true);
        usersTextBox = new JTextArea();
        usersTextBox.setLineWrap(true);
        usersTextBox.setWrapStyleWord(true);
        messageLabel = new JLabel("Message");
        usersLabel = new JLabel("Users");

        panel.add(usersLabel);
        panel.add(usersTextBox);
        panel.add(messageLabel);
        panel.add(messageTextBox);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER , panel);

        frame.setSize(800, 800);
        frame.pack();
        frame.setVisible(true);
    }


    private class MessageReceiver implements Runnable{
        private Socket socket;

        public MessageReceiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            InputStreamReader inputStreamReader = null;
            String message = null;
            String registerMessage = null;
            try {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                clientWriters.add(writer);
                registerMessage = reader.readLine();
                java.lang.System.out.println("First Message from client is - " + registerMessage);
                String user = registerMessage.split(":")[1];
                mapOfUsersAndCorrespondingWriter.put(user, writer);
                System.out.println("User to be registered is " + user);
                if(!users.contains(user)) {
                    users.add(user);
                    usersTextBox.append(user + "\n");
                }
                broadcastUsersList(users);
                PrintWriter printWriter = null;
                while ((message = reader.readLine())!= null){
                    System.out.println("Message received from client " + message);
                    String[] split = message.split(":");
                    printWriter = mapOfUsersAndCorrespondingWriter.get(split[1]);
                    messageTextBox.append(message + "\n");
                    if(!split[2].isEmpty()){
                        printWriter.println(split[0] + ":" + split[2]);
                        printWriter.flush();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    private void broadcastUsersList(Set<String> users) {
        String message = "Users";
        System.out.println("No of users "  + users.size());
        for (Iterator<String> iterator = users.iterator(); iterator.hasNext(); ) {
            message= message + ":";
            String user = iterator.next();
            System.out.println("Current user is " + user);
            message = message + user;
        }
        System.out.println("About to broadcast users message : " + message);
        broadcastMessage(message);

    }

    private void broadcastMessage(String message) {
        for (Iterator<PrintWriter> iterator = clientWriters.iterator(); iterator.hasNext(); ) {
            PrintWriter printWriter = iterator.next();
            printWriter.println(message);
            printWriter.flush();
        }
    }
}
