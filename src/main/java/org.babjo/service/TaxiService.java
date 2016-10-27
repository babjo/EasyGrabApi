package org.babjo.service;

import org.babjo.domain.Location;
import org.babjo.domain.TaxiData;
import org.babjo.dto.GetTaxiDataNearMeRequestDTO;
import org.babjo.dto.GetTaxiDataNearMeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LCH on 2016. 10. 23..
 */
@Service
public class TaxiService {

    @Autowired
    MongoTemplate mongoTemplate;

    public GetTaxiDataNearMeResponseDTO getTextDataNearMe(GetTaxiDataNearMeRequestDTO getTaxiDataNearMeRequestDTO) {

        //longitude, latitude
        GeoJsonPoint point = new GeoJsonPoint(getTaxiDataNearMeRequestDTO.getLongitude(), getTaxiDataNearMeRequestDTO.getLatitude());
        List<Location> locations = mongoTemplate.find(new Query(Criteria.where("geoJsonLineString")
                .near(point).maxDistance(1000 * getTaxiDataNearMeRequestDTO.getDistance())), Location.class);

        Map<String, GetTaxiDataNearMeResponseDTO.Result> map = new HashMap();
        for (Location l : locations) {
            GetTaxiDataNearMeResponseDTO.Result r = new GetTaxiDataNearMeResponseDTO.Result();
            List<GetTaxiDataNearMeResponseDTO.Point> points = new ArrayList();
            for(Point p : l.getGeoJsonLineString().getCoordinates()) points.add(new GetTaxiDataNearMeResponseDTO.Point(p.getX(), p.getY()));
            r.setPoints(points);
            map.put(l.getTLinkId(), r);
        }

        Criteria c = Criteria.where("tLinkId").in(map.keySet());
        if (getTaxiDataNearMeRequestDTO.getDay() != null)
            c = c.and("day").is(getTaxiDataNearMeRequestDTO.getDay());
        if (getTaxiDataNearMeRequestDTO.getTime() != null)
            c = c.and("time").is(getTaxiDataNearMeRequestDTO.getTime());

        List<TaxiData> taxiDataList = mongoTemplate.find(new Query(c), TaxiData.class);
        for (TaxiData taxiData : taxiDataList) {
            GetTaxiDataNearMeResponseDTO.Result r = map.get(taxiData.getTLinkId());
            r.setCntOff(r.getCntOff() + taxiData.getCntOff());
            r.setCntOn(r.getCntOn() + taxiData.getCntOn());
        }

        return new GetTaxiDataNearMeResponseDTO(map.values());
    }
}
