package vn.tuanphampp9.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.tuanphampp9.jobhunter.domain.Subscriber;
import vn.tuanphampp9.jobhunter.service.SubscriberService;
import vn.tuanphampp9.jobhunter.util.SecurityUtil;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create new subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {
        boolean isExist = this.subscriberService.isEmailExist(subscriber.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email is already exist");
        }
        return ResponseEntity.ok(this.subscriberService.handleCreateSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber)
            throws IdInvalidException {
        Subscriber oldSubscriber = this.subscriberService.handleFindSubscriberById(subscriber.getId());
        if (oldSubscriber == null) {
            throw new IdInvalidException("Subscriber not found");
        }
        return ResponseEntity.ok(this.subscriberService.handleUpdateSubscriber(subscriber, oldSubscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok().body(this.subscriberService.handleFindByEmail(email));
    }

}
