package org.babjo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * Created by LCH on 2016. 10. 23..
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTaxiDataNearMeResponseDTO {

    private Collection<Result> resultList;

    @Data
    public static class Result{
        private List<Point> points;
        private int cntOn = 0;
        private int cntOff = 0;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Point{
        private double x;
        private double y;
    }
}
