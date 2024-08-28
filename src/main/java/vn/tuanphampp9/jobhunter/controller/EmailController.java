package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.tuanphampp9.jobhunter.service.EmailService;
import vn.tuanphampp9.jobhunter.service.SubscriberService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    // @Scheduled(cron = "*/60 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("tuanphampp9@gmail.com", "test thymeleaf",
        // "<h1>check hello</h1>", false,
        // true);
        // this.emailService.sendEmailFromTemplateSync("tuanphampp9@gmail.com", "test
        // thymeleaf", "job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "Email sent successfully";
    }

}
