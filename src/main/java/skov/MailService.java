package skov;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.util.ArrayList;

public class MailService {

    public static void main(String[] args) {
        new MailService().sendMail("testing123", "hejsa!", args);
    }

    public Boolean sendMail(String subject, String msg, String[] toMail) {
        if (toMail.length == 0) {
            System.out.println("There is NO toMail specified. I will not send any mails !!");
            return true;
        }
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("relaygw.nykreditnet.net");
        //email.setSmtpPort(587); //use default
        try {
            email.setFrom("alsk@nykredit.dk", "ALSK automatic mail service");
            //email.addTo("pbn@nykredit.dk");
            //email.addCc("alsk@nykredit.dk");
            email.addTo(toMail);
            email.setSubject(subject);
            email.setMsg(msg);
            email.send();
            System.out.println("Sent message successfully....");
            return true;

        } catch (EmailException mex) {
            mex.printStackTrace();
            return false;
        }
    }
}