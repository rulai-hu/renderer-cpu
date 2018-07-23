package shading;

import polygon.Polygon;
import windowing.graphics.Color;

public class FlatPixelShader implements PixelShader {
	private Color color;
	
	@Override
	public Color shade(Polygon polygon, double z, double w1, double w2, double w3) {
		return color;
	}

	@Override
	public void precompute(Polygon polygon) {
		color = polygon.getSurfaceColor();
	}
}
