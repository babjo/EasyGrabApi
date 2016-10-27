package org.babjo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by LCH on 2016. 10. 23..
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiData {

    @Id
    private String id;

    @Indexed
    private String tLinkId;

    private int day;
    private int time;
    private int cntOn;
    private int cntOff;

    public TaxiData(String tLinkId, int day, int time, int cntOn, int cntOff){
        this(null, tLinkId, day, time, cntOn, cntOff);
    }
}
