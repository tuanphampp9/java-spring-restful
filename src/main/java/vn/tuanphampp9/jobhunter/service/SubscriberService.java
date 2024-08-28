package vn.tuanphampp9.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.tuanphampp9.jobhunter.domain.Job;
import vn.tuanphampp9.jobhunter.domain.Skill;
import vn.tuanphampp9.jobhunter.domain.Subscriber;
import vn.tuanphampp9.jobhunter.domain.Response.email.ResEmailJob;
import vn.tuanphampp9.jobhunter.repository.JobRepository;
import vn.tuanphampp9.jobhunter.repository.SkillRepository;
import vn.tuanphampp9.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber handleCreateSubscriber(Subscriber subscriber) {
        // check skills
        if (subscriber.getSkills() != null) {
            List<Long> listSkillIds = subscriber.getSkills().stream().map(skill -> skill.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(listSkillIds);
            subscriber.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber newSub, Subscriber oldSub) {
        // check skills
        if (newSub.getSkills() != null) {
            List<Long> listSkillIds = newSub.getSkills().stream().map(skill -> skill.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(listSkillIds);
            oldSub.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(oldSub);
    }

    public boolean isEmailExist(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber handleFindSubscriberById(Long id) {
        return this.subscriberRepository.findById(id).orElse(null);
    }

    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron() {
    // System.out.println("Cron job is running");
    // }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompanyEmail(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> listSkills = job.getSkills();
        List<ResEmailJob.SkillEmail> listSkillEmails = listSkills.stream()
                .map(skill -> new ResEmailJob.SkillEmail(skill.getName())).collect(Collectors.toList());
        res.setSkillEmails(listSkillEmails);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public Subscriber handleFindByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

}
