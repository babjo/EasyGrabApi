package org.babjo;

import org.babjo.domain.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;


import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by LCH on 2016. 10. 23..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/test-context.xml"})
public class MongoDBGeoTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Before
    public void setUp(){
        mongoTemplate.dropCollection(Location.class);
        mongoTemplate.indexOps(Location.class).ensureIndex(new GeospatialIndex("geoJsonLineString").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        double DISTANCE = 0.010000;
        GeoJsonLineString GeoJsonLineString = new GeoJsonLineString(
                new Point(13.405838, 52.531261),
                new Point(13.405838+DISTANCE, 52.531261+DISTANCE)
                );
        mongoTemplate.insert(new Location("Berlin", GeoJsonLineString));

        GeoJsonLineString = new GeoJsonLineString(
                new Point(6.921272, 50.960157),
                new Point(6.921272+DISTANCE, 50.960157+DISTANCE)
                );
        mongoTemplate.insert(new Location("Cologne", GeoJsonLineString));

        GeoJsonLineString = new GeoJsonLineString(
                new Point(6.810036, 51.224088),
                new Point(6.810036+DISTANCE, 51.224088+DISTANCE));
        mongoTemplate.insert(new Location("Dsseldorf", GeoJsonLineString));
    }

    private double KM1 = 1000;
    @Test
    public void geoTest1(){
        // when
        GeoJsonPoint point = new GeoJsonPoint(6.810036, 51.224088);
        List<Location> locations = mongoTemplate
                .find(new Query(Criteria.where("geoJsonLineString").near(point)
                .maxDistance(KM1)), Location.class);

        // then
        assertLocations(locations, "Dsseldorf");
    }

    @Test
    public void geoTest2(){
        // when
        GeoJsonPoint point = new GeoJsonPoint(6.810036, 51.224088);
        List<Location> locations = mongoTemplate
                .find(new Query(Criteria.where("geoJsonLineString").near(point)
                        .maxDistance(KM1 * 70)), Location.class);

        // then
        assertLocations(locations, "Dsseldorf", "Cologne");
    }

    private static void assertLocations(List<Location> locations, String... ids) {
        assertThat( locations, notNullValue() );
        out("-----------------------------");
        for (Location l : locations) {
            out(l);
        }
        assertThat("Mismatch location count", ids.length, is(locations.size()));
        for (String id : ids) {
            assertThat("Location " + id + " not found", containsTLinkId(locations, id), is(true));
        }
    }

    private static boolean containsTLinkId(List<Location> locations, String id){
        for (Location l : locations) if(l.getTLinkId().equals(id)) return true;
        return false;
    }

    private static void out(Object o) {
        System.out.println(o);
    }

}
