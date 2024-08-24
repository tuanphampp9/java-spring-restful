package vn.tuanphampp9.jobhunter.domain.Response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import vn.tuanphampp9.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateDTO {
    private long id;
    private String name;
    private int age;
    private String address;
    private GenderEnum gender;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private company company;

    @Getter
    @Setter
    public static class company {
        private long id;
        private String name;
    }
}
