package tn.esprit.spotup.activities;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    public static void sendEmail(String recipient, String subject, String message) {
        // Vos identifiants Gmail
        final String username = "raniabensalem53@gmail.com"; // Remplacez par votre e-mail
        final String password = "eyao cpku zitv tfpo"; // Utilisez le mot de passe d'application

        // Configuration des propriétés SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Ensure STARTTLS is enabled
        props.put("mail.smtp.port", "587");             // Correct port for STARTTLS
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");  // Gmail SMTP host
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true"); // Active les logs détaillés


        // Créer une session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Créer un message e-mail
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            // Envoyer l'e-mail
            Transport.send(mimeMessage);

            System.out.println("E-mail envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}



//package tn.esprit.spotup.activities;
//import java.util.Properties;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import javax.mail.Message;
//import javax.mail.Session;
//
//public class MailSender {
//
//    public static void sendEmail(String recipient, String subject, String message) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String host = "sandbox.smtp.mailtrap.io";
//                String port = "465";
//                String username = "a7585c242b5a97";
//                String password = "d5265dce8cdb86";
//                //String to = "rania.bensalem.1@esprit.tn"; // Change to a valid email
//
//                Properties props = new Properties();
//                props.put("mail.smtp.auth", "true");
//                props.put("mail.smtp.starttls.enable", "true");
//                props.put("mail.smtp.host", host);
//                props.put("mail.smtp.port", port);
//                props.put("mail.transport.protocol", "smtp");
//
//                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(username, password);
//                    }
//                });
//
//                try {
//            // Créer un message e-mail
//            Message mimeMessage = new MimeMessage(session);
//            mimeMessage.setFrom(new InternetAddress(username));
//            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
//            mimeMessage.setSubject(subject);
//            mimeMessage.setText(message);
//
//            // Envoyer l'e-mail
//            Transport.send(mimeMessage);
//
//            System.out.println("E-mail envoyé avec succès !");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//            }
//        });
//    }
//}