package pt.unl.fct.di.adc.silvanus.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

public class PolygonUtils {

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
}
