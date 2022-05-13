package io.renren.modules.business.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskCountDTO {
    private String title;

    private String icon;

    private int count;

    private String color;
}
