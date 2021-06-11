package skov;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

public class MailService {

    public static void main(String[] args) {
        new MailService().sendMail("testing123", "hejsa!");
    }

    public Boolean sendMail(String subject, String msg) {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("relaygw.nykreditnet.net");
        //email.setSmtpPort(587); //use default
        try {
            email.setFrom("alsk@nykredit.dk", "ALSK automatic mail service");
            email.addTo("pbn@nykredit.dk");
            email.addCc("alsk@nykredit.dk");
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