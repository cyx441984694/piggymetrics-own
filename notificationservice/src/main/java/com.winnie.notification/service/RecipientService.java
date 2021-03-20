package com.winnie.notification.service;

import com.winnie.notification.domain.NotificationType;
import com.winnie.notification.domain.Recipient;

import java.util.List;

public interface RecipientService {

    Recipient findByAccountName(String accountName);

    List<Recipient> findReadyToNotify(NotificationType type);

    Recipient save(String accountName, Recipient recipient);

    void markNotified(NotificationType type, Recipient recipient);

}
