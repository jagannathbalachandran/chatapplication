package com.chat;import javax.swing.BoxLayout;import javax.swing.JButton;import javax.swing.JFrame;import javax.swing.JLabel;import javax.swing.JPanel;import javax.swing.JTextArea;import javax.swing.JTextField;
import java.awt.BorderLayout;import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;import java.lang.Override;import java.lang.String;import java.lang.System;

/**
 * Created by jbalacha on 07/12/15.
 */
public class MyJFrame extends JFrame{
    private String recipient;
    private final JTextField messageTextBox;
    private String clientName;
    private PrintWriter clientWriter;
    JTextArea chatWindow;

    public MyJFrame(String clientName, String toChat, PrintWriter clientWriter) {
        super(clientName + "- Chat with "  + toChat);
        this.clientName = clientName;
        this.clientWriter = clientWriter;
        this.recipient =  toChat;
        JPanel jPanel = new JPanel();
        messageTextBox = new JTextField();
        chatWindow = new JTextArea();
        chatWindow.setWrapStyleWord(true);
        chatWindow.setLineWrap(true);
        JButton sendButton = new JButton("Send");;
        JLabel chatWindowLabel = new JLabel("Chat Window");
        JLabel messageLabel = new JLabel("Message");
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(chatWindowLabel);
        jPanel.add(chatWindow);
        jPanel.add(messageLabel);
        jPanel.add(messageTextBox);
        jPanel.add(sendButton);
        this.getContentPane().add(BorderLayout.CENTER , jPanel);

        sendButton.addActionListener(new SendButtonListener(this));

        this.setSize(800, 800);
        this.pack();
        this.setVisible(true);
    }

    private class SendButtonListener implements ActionListener {
        private MyJFrame myJFrame;

        public SendButtonListener(MyJFrame myJFrame) {

            this.myJFrame = myJFrame;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Sending message to server " + messageTextBox.getText());
            clientWriter.println(clientName + ":" + recipient + ":" + messageTextBox.getText());
            clientWriter.flush();
            myJFrame.appendChatMessage("me:" + messageTextBox.getText() + "\n");
            messageTextBox.setText("");

        }
    }

    public void appendChatMessage(String message) {
        chatWindow.append(message);
    }
}
