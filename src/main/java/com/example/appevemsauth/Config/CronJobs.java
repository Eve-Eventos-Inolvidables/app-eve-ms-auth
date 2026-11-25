package com.example.appevemsauth.Config;

import com.example.appevecommon.Models.User.User;
import com.example.appevecommon.Service.Email.EmailService;
import com.example.appevemsauth.Repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CronJobs {

    @Autowired
    private EmailService emailService;

    @Autowired
    private IUserRepository userRepository;
    @Scheduled(cron ="${app.jobs.notify-events.cron:0 */1 * * * *}", zone = "America/Asuncion")
    public void notifyUsers() {
        int page = 1;
        log.info("Iniciando job de enviar notificacion de eventos próximos");
        List<User> users = userRepository.findAll(PageRequest.of(page,10)).stream().toList();
        while(! users.isEmpty() ){

            final int copy = page;
            users.forEach(u ->{
                emailService.asyncSendEmailSafely(u.getEmail(),"Atención Usuarios de EVE!",
                        """
                ¿Ya tenés planes para los próximos días o estás buscando algo diferente para hacer en Paraguay? En EVE ya están disponibles los nuevos eventos cargados en la plataforma y queremos invitarte a que entres a explorar todo lo que se viene cerca tuyo.

                Desde encuentros culturales, música en vivo, ferias y actividades al aire libre hasta talleres y propuestas para compartir con amigos o en familia, en EVE reunimos en un solo lugar las mejores opciones para que no te quedes afuera de nada.

                ¿Qué podés hacer hoy mismo dentro de la app?
                - Explorar eventos próximos: Filtrá por fecha o categoría y encontrá planes ideales para tu semana o fin de semana.
                - Ver todos los detalles al instante: Consultá horarios, ubicaciones, información del organizador y todo lo que necesitás saber antes de ir.
                - Guardar y organizar tus favoritos: Marcá los eventos que más te interesan para tenerlos siempre a mano y recibir recordatorios a tiempo.

                No dejes que te lo cuenten después. Entrá ahora a EVE, mirá la agenda actualizada y elegí tu próximo plan:
               
                ¡Nos vemos en el próximo evento!
                El equipo de EVE
                """);
            });
            page+=1;
            users = userRepository.findAll(PageRequest.of(page,10)).stream().toList();

        }
        log.info("Finalizó job de enviar notificacion de eventos próximos");

    }
}