package com.example.common;

import com.example.config.Environment;
import com.example.models.TestResult;
import com.sun.mail.smtp.SMTPTransport;
import j2html.tags.ContainerTag;
import lombok.extern.log4j.Log4j2;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import static j2html.TagCreator.*;

/**
 * This class helps you in sending an email
 * @author Armando
 *
 */
@Log4j2
public class EmailHelper {

    private static final String EMAIL_FROM = "armando.serna@wolterskluwer.com";
    private static final String EMAIL_TO = "armando.serna@wolterskluwer.com";
    private static final String EMAIL_TO_CC = "armando.serna@wolterskluwer.com";

    private static final String EMAIL_SUBJECT = "Login Verification - %s - %s";
    private static final String EMAIL_TEXT = "Please find the results for login verification";

    private final List<TestResult> results;
    private final String startTime;
    private final String endTime;

    public EmailHelper(List<TestResult> results, String startTime, String endTime) {
        this.results = results;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * This function just sends an email
     */
    public String sendEmail() {
        log.info("Sending email");
        String serverResponse = null;

        InputStream fileInputStream =
                getClass().getClassLoader().getResourceAsStream(Environment.CONFIG_FILE_NAME);
        Properties configProps = new Properties();
        String server;
        String username;
        String password;
        try {
            configProps.load(fileInputStream);
            server = configProps.getProperty("mail.smtp.host", "smtp.mailtrap.io");
            username = configProps.getProperty("mail.smtp.host.username", "username");
            password = configProps.getProperty("mail.smtp.host.password", "password");
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (NullPointerException e) {
            log.info("config.properties file does not exists, using default values", e);
            server = "smtp.mailtrap.io";
            username = "username";
            password = "password";
        } catch (IOException e) {
            log.info("error while reading config.properties, using default values ", e);
            server = "smtp.mailtrap.io";
            username = "username";
            password = "password";
        }

        Session session = Session.getInstance(configProps, null);
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(EMAIL_FROM));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(EMAIL_TO, false));
            msg.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(EMAIL_TO_CC, false));
            String subject = String.format(EMAIL_SUBJECT, Environment.envName, Environment.dateTimestamp);
            msg.setSubject(subject);

            ZipHelper.zipFolder(
                    Environment.screenshotBasePath, Environment.screenshotsZipFilePath);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(setupText(), "text/html");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(Environment.screenshotsZipFilePath));

            multipart.addBodyPart(attachmentPart);
            multipart.addBodyPart(mimeBodyPart);

            msg.setContent(multipart);

            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(server, username, password);
            t.sendMessage(msg, msg.getAllRecipients());
            serverResponse = t.getLastServerResponse();
            log.info("SMTP response: " + serverResponse);
            t.close();
        } catch (MessagingException | IOException e) {
            log.error(e);
        }

        return serverResponse;
    }

    private String setupText() throws MessagingException {
        File stylesFile = getStylesFile();
        return html(styleWithInlineFile(stylesFile.getPath()), body(
                p(EMAIL_TEXT),
                p("Start time: " + startTime),
                p("End time: " + endTime),
                table(
                        thead(
                                th("Application Name"),
                                th("URL"),
                                th("Result")
                        ),
                        tbody(results.stream().map(result ->
                                tr(
                                        td(result.getApplicationName()),
                                        td(a(result.getUrl()).withHref(result.getUrl())),
                                        iffElse(result.isTestResult(),
                                                td("PASS").withClass("pass"),
                                                td("FAIL").withClass("fail"))
                                        )
                                ).toArray(ContainerTag[]::new)
                        )
                )
        )).render();
    }

    private File getStylesFile() {
        URL stylesUrl = getClass().getClassLoader().getResource("styles.css");
        File file;
        try {
            file = new File(stylesUrl != null ? stylesUrl.toURI() : null);
        } catch (URISyntaxException e) {
            file = new File(stylesUrl.getPath());
        }
        return file;
    }
}
