package org.babjo;

import com.mongodb.client.model.geojson.LineString;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.babjo.domain.Location;
import org.babjo.domain.TaxiData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;


/**
 * Created by LCH on 2016. 10. 23..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class InputData {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void inputTLinkIdData() throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource("Link_WGS84_Link_Info_150M.txt").toURI());
        Charset charset = Charset.forName("utf-8");
        List<String> lines = Files.readAllLines(path, charset);

        mongoTemplate.dropCollection(Location.class);
        // ensure geospatial index
        mongoTemplate.indexOps(Location.class).ensureIndex(new GeospatialIndex("geoJsonLineString").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        for(int i=1; i<lines.size(); i++){
            String[] elements = lines.get(i).split(",", -1);
            // T_LINK_ID,X_MAX,Y_MAX,X_MIN,Y_MIN,X_PART,Y_PART
            String tLink = elements[0];
            List<Point> points = new ArrayList();

            /*
            double lngMax = Double.parseDouble(elements[1]); // 경도
            double latMax = Double.parseDouble(elements[2]); // 위도
            points.add(new Point(lngMax, latMax));

            double lngMin = Double.parseDouble(elements[3]); // 경도
            double latMin = Double.parseDouble(elements[4]); // 위도
            points.add(new Point(lngMin, latMin));
            */

            String[] lngPartsStr = elements[5].split("\\^");
            String[] latPartsStr = elements[6].split("\\^");
            for(int j=0; j<lngPartsStr.length; j++){
                double lngPart = Double.parseDouble(lngPartsStr[j]);
                double latPart = Double.parseDouble(latPartsStr[j]);
                points.add(new Point(lngPart, latPart));
            }

            GeoJsonLineString geoJsonLineString = new GeoJsonLineString(points);

            mongoTemplate.insert(new Location(tLink, geoJsonLineString));
        }
    }

    @Test
    public void inputTaxiData() throws IOException, URISyntaxException {
        mongoTemplate.dropCollection(TaxiData.class);
        mongoTemplate.indexOps(TaxiData.class).ensureIndex(new Index("tLinkId", Sort.Direction.ASC));

        // part-00000 ~ part-01227
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Future<String>> list = new ArrayList();
        for(int i=0; i<1228; i++){
            //submit Callable tasks to be executed by thread pool
            Future<String> future = executor.submit(new InputTaxiTask(i));
            //add Future to the list, we can get return value using Future
            list.add(future);
        }

        for(Future<String> fut : list){
            try {
                //print the return value of Future, notice the output delay in console
                // because Future.get() waits for task to get completed
                System.out.println(new Date()+ "::"+fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
        System.out.println("-----finish-----");
    }

    @AllArgsConstructor
    class InputTaxiTask implements Callable<String>{
        private int num;
        @Override
        public String call() throws Exception {
            String fileName = String.format("part-%05d", num);
            Path path = Paths.get(ClassLoader.getSystemResource("taxi/"+fileName).toURI());
            Charset charset = Charset.forName("utf-8");
            List<String> lines = Files.readAllLines(path, charset);

            System.out.println(fileName + " start");
            for (int i = 0; i < lines.size(); i++) {
                String[] elements = lines.get(i).split(",", -1);
                // T_Link_ID,Day,Time,CntOn,CntOff
                String tLink = elements[0];
                int day = Integer.parseInt(elements[1]);
                int time = Integer.parseInt(elements[2]);
                int cntOn = Integer.parseInt(elements[3]);
                int cntOff = Integer.parseInt(elements[4]);
                mongoTemplate.insert(new TaxiData(tLink, day, time, cntOn, cntOff));
            }
            return fileName + " is Done";
        }
    }

}
