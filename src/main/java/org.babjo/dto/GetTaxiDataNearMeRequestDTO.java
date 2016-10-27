package org.babjo.dto;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by LCH on 2016. 10. 23..
 */
@Data
public class GetTaxiDataNearMeRequestDTO {

    @NotNull // 위도
    private Double latitude;

    @NotNull // 경도
    private Double longitude;

    @NotNull
    private Double distance; // km

    private Integer day;
    private Integer time;
}
