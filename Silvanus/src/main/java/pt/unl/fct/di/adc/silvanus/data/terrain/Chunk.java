package pt.unl.fct.di.adc.silvanus.data.terrain;

import org.locationtech.jts.geom.Polygon;
import pt.unl.fct.di.adc.silvanus.util.PolygonUtils;

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
		this.polygon = PolygonUtils.box(topY, bottomY, leftX, rightX);
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

	public String toString() {
		return String.format("%s: { %.2f ; %.2f ; %.2f ; %.2f }", getIdChunk(), getleftX(), getrightX(),
				gettopY(), getbottomY());
	}

	public Polygon getChunkAsPolygon() {
		return polygon;
	}

}
