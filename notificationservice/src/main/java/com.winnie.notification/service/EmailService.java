package com.winnie.notification.service;


import com.winnie.notification.domain.NotificationType;
import com.winnie.notification.domain.Recipient;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailService {

    void send(NotificationType type, Recipient recipient, String attachment) throws MessagingException, IOException;
}
