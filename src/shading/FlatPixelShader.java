package shading;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public class FlatPixelShader implements PixelShader {
	Polygon polygon;
	
	@Override
	public Color shade(Vertex3D current) {
		return polygon.get(0).getColor();
	}

	@Override
	public void setBarycentricCoords(double w1, double w2, double w3) {
		return;
	}

	@Override
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

}
