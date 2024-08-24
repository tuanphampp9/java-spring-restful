package vn.tuanphampp9.jobhunter.domain.Response;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.tuanphampp9.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String location;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant updatedAt;
    private String updatedBy;
    private Instant createdAt;
    private String createdBy;
    private List<String> skills;
}
