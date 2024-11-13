package tn.esprit.spotup.fragments.salsabil;

import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public void sendEmail(String recipientEmail, String subject, String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String host = "sandbox.smtp.mailtrap.io";
                String port = "587";
                String username = "9a5a874d7f0f31";
                String password = "8d238e6b30c8ba";
                String to = "salsabil.zaabar@esprit.tn"; // Change to a valid email

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.port", port);
                props.put("mail.transport.protocol", "smtp");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                    message.setSubject(subject);
                    message.setText(body);

                    Transport.send(message);
                    Log.d("EmailSender", "Email sent successfully!");

                } catch (MessagingException e) {
                    Log.e("EmailSender", "Failed to send email", e);
                }
            }
        }).start(); // Start the new thread here
    }
}
