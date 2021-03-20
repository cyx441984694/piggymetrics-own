package com.winnie.notification.service;


import com.winnie.notification.domain.NotificationType;
import com.winnie.notification.domain.Recipient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.stream.binder.JavaClassMimeTypeUtils;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private Environment env;

    @Mock
    private JavaMailSender javaMailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> captor;

    @Before
    public void setup(){
        initMocks(this);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));}

    @Test
    public void shouldSendRemindMails() throws MessagingException, IOException {
        final String subject = "subject";
        final String text = "text";
        final String attachment = "attachment.json";

        Recipient recipient = new Recipient();
        recipient.setEmail("test@test.com");
        recipient.setAccountName("test");

        when(env.getProperty(NotificationType.REMIND.getSubject())).thenReturn(subject);
        when(env.getProperty(NotificationType.REMIND.getText())).thenReturn(text);
        when(env.getProperty(NotificationType.REMIND.getAttachment())).thenReturn(attachment);

        emailService.send(NotificationType.REMIND, recipient,"{\"name\":\"test\"");

        verify(javaMailSender).send(captor.capture());

        MimeMessage message = captor.getValue();
        assertEquals(subject, message.getSubject());

    }

    @Test
    public void shouldSendBackupEmail() throws MessagingException,IOException{
        final String subject = "subject";
        final String text = "text";
        final String attachment = "attachment.json";

        Recipient recipient = new Recipient();
        recipient.setEmail("test@test.com");
        recipient.setAccountName("test");

        when(env.getProperty(NotificationType.BACKUP.getSubject())).thenReturn(subject);
        when(env.getProperty(NotificationType.BACKUP.getText())).thenReturn(text);
        when(env.getProperty(NotificationType.BACKUP.getAttachment())).thenReturn(attachment);

        emailService.send(NotificationType.BACKUP, recipient,"{\"name\":\"test\"");

        verify(javaMailSender).send(captor.capture());

        MimeMessage message = captor.getValue();
        assertEquals(subject, message.getSubject());
    }


}
