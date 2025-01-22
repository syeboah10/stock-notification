package syeb.notifications;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class GmailApiEmailSender {

    private final String username;
    private final String password;
    private final String host = "smtp.gmail.com";
    private final int port = 587;

    public GmailApiEmailSender() {
        this.username = "kembathedon5";
        this.password = null;
    }

    public void sendEmail(String to, String subject, String body) {
        // Set properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        // Get session
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username + "@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}