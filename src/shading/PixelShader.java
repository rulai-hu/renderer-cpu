package shading;

import polygon.Polygon;
import windowing.graphics.Color;

public interface PixelShader {
	Color shade(Polygon polygon, double z, double w1, double w2, double w3);
	void precompute(Polygon polygon);
}
