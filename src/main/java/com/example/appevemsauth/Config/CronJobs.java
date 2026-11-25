package com.example.appevemsauth.Config;

import com.example.appevecommon.Service.Email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CronJobs {

    @Autowired
    private EmailService emailService;

    @Scheduled(cron ="${app.jobs.notify-events.cron:0 */1 * * * *}", zone = "America/Asuncion")
    public void notifyUsers() {
        log.info("Iniciando job de enviar notificacion de eventos próximos");
        emailService.asyncSendEmailSafely("matias.marecos@fiuni.edu.py","CRON2","PRUEBA CRON 2 jj");
        log.info("Finalizó job de enviar notificacion de eventos próximos");
    }
}