package com.winnie.notification.service;


import com.google.common.collect.ImmutableList;
import com.winnie.notification.client.AccountServiceClient;
import com.winnie.notification.domain.NotificationType;
import com.winnie.notification.domain.Recipient;
import io.netty.handler.codec.MessageAggregationException;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.mail.MessagingException;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private RecipientService recipientService;

    @Mock
    private EmailService emailService;

    @Before
    public void setup(){
        initMocks(this);
    }


    @Test
    public void shouldSendBackupNotifications() throws MessagingException, IOException,InterruptedException {
        String attachment = "attachment";

        Recipient account1 = new Recipient();
        account1.setAccountName("good");

        Recipient account2 = new Recipient();
        account2.setAccountName("bad");

        final NotificationType type = NotificationType.BACKUP;

        when(accountServiceClient.getAccount(account1.getAccountName())).thenReturn(attachment);
        when(accountServiceClient.getAccount(account2.getAccountName())).thenThrow(new RuntimeException());

        when(recipientService.findReadyToNotify(type)).thenReturn(ImmutableList.of(account1, account2));

        notificationService.sendBackupNotifications();

        verify(emailService, timeout(100)).send(type, account1, attachment);
        verify(recipientService, timeout(100)).markNotified(type, account1);

        verify(recipientService, never()).markNotified(type, account2);
    }
//The same as remind one
    @Test
    public void shouldSendRemindNotifications() throws MessagingException, IOException {
        String attachment = "attachment";

        Recipient account1 = new Recipient();
        account1.setAccountName("good");

        Recipient account2 = new Recipient();
        account2.setAccountName("bad");

        final NotificationType type = NotificationType.REMIND;

        when(recipientService.findReadyToNotify(type)).thenReturn(ImmutableList.of(account1, account2));
        doThrow(new RuntimeException()).when(emailService).send(type, account2, null);

        when(accountServiceClient.getAccount(account1.getAccountName())).thenReturn(attachment);

        notificationService.sendRemindNotifications();

        verify(emailService, timeout(100)).send(type, account1, null);
        verify(recipientService, timeout(100)).markNotified(type, account1);

        verify(recipientService, never()).markNotified(type, account2);
    }



}
