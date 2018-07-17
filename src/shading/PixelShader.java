package shading;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public interface PixelShader {
	public Color shade(Vertex3D current);
	public void setBaryocentricCoords(double w1, double w2, double w3);
	public void setPolygon(Polygon polygon);
}
