package vn.tuanphampp9.jobhunter.domain.Response.resume;

import vn.tuanphampp9.jobhunter.util.constant.ResumeStateEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class ResFetchResumeDTO {
    private long id;
    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private UserResume user;
    private JobResume job;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class UserResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class JobResume {
        private long id;
        private String name;
    }

}
