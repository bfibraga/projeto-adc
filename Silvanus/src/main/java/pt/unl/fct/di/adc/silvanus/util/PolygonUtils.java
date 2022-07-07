package pt.unl.fct.di.adc.silvanus.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.data.terrain.LatLng;

public class PolygonUtils {

    public static Polygon polygon(LatLng[] points){
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[points.length+1];
        for (int i = 0 ; i < points.length ; i++) {
            coordinates[i] = new Coordinate(points[i].getLng(), points[i].getLat());
        }
        coordinates[points.length] = new Coordinate(points[0].getLng(), points[0].getLat());
        return geometryFactory.createPolygon(coordinates);
    }

    public static Polygon box(float top, float bottom, float left, float right ){
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(bottom, left);
        coordinates[1] = new Coordinate(top, left);
        coordinates[2] = new Coordinate(top, right);
        coordinates[3] = new Coordinate(bottom, right);
        coordinates[4] = new Coordinate(coordinates[0].getX(), coordinates[0].getY());
        return geometryFactory.createPolygon(coordinates);
    }

    public static LatLng centroid(LatLng[] points){
        int size = points.length;
        float lng = 0;
        float lat = 0;
        for (int i = 0; i < size; i++) {
            LatLng point = points[i];
            lng += point.getLng();
            lat += point.getLat();
        }
        lng /= size;
        lat /= size;
        return new LatLng(lat,lng);
    }
}
