package com.winnie.notification.repository;


import com.google.common.collect.ImmutableMap;
import com.netflix.discovery.converters.Auto;
import com.winnie.notification.domain.Frequency;
import com.winnie.notification.domain.NotificationSettings;
import com.winnie.notification.domain.NotificationType;
import com.winnie.notification.domain.Recipient;
import org.apache.commons.lang.time.DateUtils;
import org.aspectj.weaver.ast.Not;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipientRepositoryTest {

    @Autowired
    private RecipientRepository recipientRepository;

    @Test
    public void shouldFindByAccountName(){
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(null);

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(Frequency.MONTHLY);
        backup.setLastNotified(null);

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup,
                NotificationType.REMIND, remind
        ));

        recipientRepository.save(recipient);

        Recipient found = recipientRepository.findByAccountName(recipient.getAccountName());
        assertEquals(recipient.getAccountName(), found.getAccountName());
        assertEquals(recipient.getEmail(), found.getEmail());
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getActive(),
                found.getScheduledNotifications().get(NotificationType.BACKUP).getActive());
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getFrequency(),
                found.getScheduledNotifications().get(NotificationType.BACKUP).getFrequency());
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getLastNotified(),
                found.getScheduledNotifications().get(NotificationType.BACKUP).getLastNotified());

        assertEquals(recipient.getScheduledNotifications().get(NotificationType.REMIND).getActive(),
                found.getScheduledNotifications().get(NotificationType.REMIND).getActive());
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.REMIND).getFrequency(),
                found.getScheduledNotifications().get(NotificationType.REMIND).getFrequency());
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified(),
                found.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified());
    }

    @Test
    public void shouldFindReadyForBackup(){
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.QUARTERLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -91));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, remind
        ));

        recipientRepository.save(recipient);

        List<Recipient> found = recipientRepository.findReadyForBackup();
        assertFalse(found.isEmpty());
    }

    @Test
    public void shouldFindReadyForRemind(){

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -8));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.REMIND, remind));

        recipientRepository.save(recipient);

        List<Recipient> found=recipientRepository.findReadyForRemind();
        assertFalse(found.isEmpty());
    }

    @Test
    public void shouldNotFindReadyForRemindWhenLastNotifiedisNotActive(){

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(false);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -8));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.REMIND, remind));

        recipientRepository.save(recipient);

        List<Recipient> found=recipientRepository.findReadyForRemind();
        assertTrue(found.isEmpty());
    }


    @Test
    public void shouldFindReadyForRemindWhenLastNotifiedisIsQuaterly() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.QUARTERLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -90));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(NotificationType.REMIND, remind));

        recipientRepository.save(recipient);

        List<Recipient> found = recipientRepository.findReadyForRemind();
        assertFalse(found.isEmpty());
    }
}
