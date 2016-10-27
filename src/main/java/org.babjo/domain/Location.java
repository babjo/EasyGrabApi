package org.babjo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.index.Indexed;


/**
 * Created by LCH on 2016. 10. 23..
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    private String id;

    @Indexed
    private String tLinkId;

    // { longitude 경도 , latitude 위도 }
    private GeoJsonLineString geoJsonLineString;

    public Location(String tLinkId, GeoJsonLineString geoJsonLineString){
        this(null, tLinkId, geoJsonLineString);
    }

}
