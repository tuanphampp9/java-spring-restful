package vn.tuanphampp9.jobhunter.domain.DTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.tuanphampp9.jobhunter.util.constant.GenderEnum;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String address;
    private int age;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
}
