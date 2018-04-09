package Mail;

import Hibernate.Entity.Dom;
import com.sun.deploy.util.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
/**
 * Created by Arnold on 10.04.2018.
 */
public class SendMail extends SendMailConfig {
    Session session = null;
    private java.lang.String from = "9832802@gmail.com";
    private java.lang.String to = "6546@mail.ru";

    public SendMail() {
        String to = "6546@mail.ru";
        String from = "9832802@gmail.com";

        Properties props = new Properties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);
    }

    public void send(ArrayList<Dom> newDom) {
        ArrayList<String> html = new ArrayList<String>();

        for (Dom dom: newDom){
            html.add(
                    "<h1>" + dom.getName() + "</h1>"
                    + "<br/> <img src=\"" + dom.getImg() + "\" />"
                    + "<br/> <b>" + dom.getCost() + "</b>"
                    + "<br/> <span>" + dom.getDate() + "</span>"
            );
        }

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject("New Apartments!");

            message.setContent(StringUtils.join(html,"<br><br>"),"text/html");

            Transport.send(message);
            System.out.println("Send message successfully");

        } catch (MessagingException e) {
            System.out.println(e);
        }

    }
}
