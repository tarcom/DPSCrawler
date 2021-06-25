package skov;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Properties;

public class MailServiceHtml {


    public static void main(String[] args) throws Exception {
        args = new String[]{"alsk@nykredit.dk", "alsk@nykredit.dk"};
        new MailServiceHtml().sendMail("testing123", "hejsa!", args);
    }

    public void sendMail(String subject, String msg, String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("There is NO toMail specified. I will not send any mails !!");
            return;
        }
        String toMail = Arrays.toString(args).substring(1, Arrays.toString(args).length() - 1);

        Properties props = new Properties();
        props.put("mail.smtp.host", "relaygw.nykreditnet.net");

        Message message = new MimeMessage(Session.getInstance(props));
        message.setFrom(new InternetAddress("alsk@nykredit.dk"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
        message.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart("related");

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("<img src=\"cid:image2\"><img src=\"cid:image1\">", "text/html");
        multipart.addBodyPart(messageBodyPart);

        BodyPart messageBodyPart2 = new MimeBodyPart();
        messageBodyPart2.setDataHandler(new DataHandler(new FileDataSource(DPSCrawler.screenshotFile2)));
        messageBodyPart2.setHeader("Content-ID", "<image2>");
        multipart.addBodyPart(messageBodyPart2);


        BodyPart messageBodyPart3 = new MimeBodyPart();
        messageBodyPart3.setDataHandler(new DataHandler(new FileDataSource(DPSCrawler.screenshotFile1)));
        messageBodyPart3.setHeader("Content-ID", "<image1>");
        multipart.addBodyPart(messageBodyPart3);

        message.setContent(multipart);
        Transport.send(message);

        System.out.println("Email sent successfully.");

    }
}