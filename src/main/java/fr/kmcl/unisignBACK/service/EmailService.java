package fr.kmcl.unisignBACK.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static fr.kmcl.unisignBACK.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.*;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Service
public class EmailService {

    /**
     * Send email to new user with his information
     * @param firstName String: new user's first name
     * @param password String: new user's password
     * @param email String: new user's email
     * @throws MessagingException: exception can be thrown while creating the message
     */
    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    /**
     * Set email properties and return and instance of email session
     * @return Session: email session instance
     */
    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getDefaultInstance(properties, null);
    }

    /**
     * Create the email message sent after creating a new user account
     * @param firstName String: new user's first name
     * @param password String: new user's password
     * @param email String: new user's email
     * @return Message: message sent to new user
     * @throws MessagingException: exception can be thrown while creating the message
     */
    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Bonjour " + firstName + ", \n \nLe mot de passe de votre nouveau compte KMCL UniSign est : " + password + "\n \nL'équipe de développement\nKonica Minolta Centre Loire");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }
}
