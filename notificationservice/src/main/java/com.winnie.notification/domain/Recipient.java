package com.winnie.notification.domain;


import com.esotericsoftware.kryo.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.Map;

@Document(collection = "recipients")
public class Recipient {

    @Id
    private String accountName;

    @NotNull
    @Email
    private String email;

    @Valid
    private Map<NotificationType, NotificationSettings> scheduledNotifications;

    public String getAccountName(){return accountName;}
    public void setAccountName(String accountName){this.accountName = accountName;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}

    public Map<NotificationType, NotificationSettings> getScheduledNotifications(){
        return scheduledNotifications;
    }
    public void setScheduledNotifications(Map<NotificationType, NotificationSettings> scheduledNotifications){
        this.scheduledNotifications = scheduledNotifications;
    }

    @Override
    public String toString(){
        return "Recipient{" +
                "accountName='" + accountName +'\'' +
                ", email='" + email + '\'' +
                '}';
    }


}
