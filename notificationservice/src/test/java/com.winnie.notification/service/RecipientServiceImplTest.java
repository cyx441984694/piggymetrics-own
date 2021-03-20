package com.winnie.notification.service;


import com.esotericsoftware.kryo.NotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.winnie.notification.domain.Frequency;
import com.winnie.notification.domain.NotificationSettings;
import com.winnie.notification.domain.NotificationType;
import com.winnie.notification.domain.Recipient;
import com.winnie.notification.repository.RecipientRepository;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecipientServiceImplTest {

    @InjectMocks
    private RecipientServiceImpl recipientService;

    @Mock
    private RecipientRepository repository;

    @Before
    public void setup(){
        initMocks(this);
    }

    @Test
    public void ShouldFindByAccountName(){
        final String accountName = "test";

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");

        when(repository.findByAccountName(recipient.getAccountName())).thenReturn(recipient);
        Recipient found = recipientService.findByAccountName(recipient.getAccountName());
        assertEquals(recipient,found);

    }

    @Test
    public void shouldSaveRecipient(){
        NotificationSettings setting1 = new NotificationSettings();
        setting1.setActive(true);
        setting1.setFrequency(Frequency.WEEKLY);
        setting1.setLastNotified(new Date());

        NotificationSettings setting2 = new NotificationSettings();
        setting2.setActive(true);
        setting2.setFrequency(Frequency.MONTHLY);
        setting2.setLastNotified(new Date());

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(
                ImmutableMap.of(NotificationType.BACKUP, setting1,
                        NotificationType.REMIND,setting2)
        );

        Recipient saved = recipientService.save("test",recipient);

        verify(repository).save(recipient);
        assertNotNull(saved.getScheduledNotifications().get(NotificationType.BACKUP).getLastNotified());
        assertEquals("test", saved.getAccountName());
    }

    @Test
    public void shouldFindReadyToNotify(){
        final List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForBackup()).thenReturn(recipients);

        List<Recipient> got= recipientService.findReadyToNotify(NotificationType.BACKUP);

        assertEquals(recipients,got);
    }

    @Test
    public void shouldMarkNotified(){
        NotificationSettings setting1 = new NotificationSettings();
        setting1.setActive(true);
        setting1.setFrequency(Frequency.WEEKLY);
        setting1.setLastNotified(new Date());

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(
                ImmutableMap.of(NotificationType.BACKUP, setting1)
        );

        recipientService.markNotified(NotificationType.BACKUP, recipient);
        assertNotNull(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getLastNotified());
        verify(repository, times(1)).save(recipient);
    }
}
