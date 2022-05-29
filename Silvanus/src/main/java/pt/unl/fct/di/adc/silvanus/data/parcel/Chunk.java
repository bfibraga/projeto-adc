package pt.unl.fct.di.adc.silvanus.data.parcel;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

public class Chunk {

	private float leftX;
	private float rightX;
	private float topY;
	private float bottomY;

	private Polygon polygon;

	private String idChunk;

	public Chunk(String idChunk, float leftX, float rightX, float topY, float bottomY) {
		this.idChunk = idChunk;
		this.leftX = leftX;
		this.rightX = rightX;
		this.topY = topY;
		this.bottomY = bottomY;
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate[] coordinates = new Coordinate[5];
		coordinates[0] = new Coordinate(bottomY, leftX);
		coordinates[1] = new Coordinate(topY, leftX);
		coordinates[2] = new Coordinate(topY, rightX);
		coordinates[3] = new Coordinate(bottomY, rightX);
		coordinates[4] = new Coordinate(coordinates[0].getX(), coordinates[0].getY());
		this.polygon = geometryFactory.createPolygon(coordinates);
	}

	/**
	 * @return the leftX
	 */
	public float getleftX() {
		return leftX;
	}

	/**
	 * @return the rightX
	 */
	public float getrightX() {
		return rightX;
	}

	/**
	 * @return the topY
	 */
	public float gettopY() {
		return topY;
	}

	/**
	 * @return the bottomY
	 */
	public float getbottomY() {
		return bottomY;
	}

	public String getIdChunk() {
		return idChunk;
	}

	public boolean pontoEstaDentro(float x, float y) {
		return (x < leftX && x > rightX) && (y < topY && y > bottomY);
	}

	public String toString() {
		String result = String.format("%s: { %.2f ; %.2f ; %.2f ; %.2f }", getIdChunk(), getleftX(), getrightX(),
				gettopY(), getbottomY());
		return result;
	}

	public Polygon getChunkAsPolygon() {
		return polygon;
	}

}
