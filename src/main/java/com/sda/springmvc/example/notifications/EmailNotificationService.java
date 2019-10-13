package com.sda.springmvc.example.notifications;

import com.sda.springmvc.example.events.UserSignupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);

    @Async
    @EventListener
    public void sendConfirmationEmail(UserSignupEvent userSignupEvent){
        LOG.info("Thread: {}", Thread.currentThread().getName());
        LOG.info("Received an event {}", userSignupEvent);

        userSignupEvent.getSource();
    }
}
