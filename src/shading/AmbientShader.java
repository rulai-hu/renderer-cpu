package shading;

import polygon.Polygon;
import shading.ShadingStyle.LightingData;
import windowing.graphics.Color;

public class AmbientShader implements FaceShader {
	private LightingData data;
	
	public AmbientShader(LightingData data) {
		this.data = data;
	}

	@Override
	public Polygon shade(Polygon polygon) {
		Color color = polygon.get(0).getColor().multiply(data.getAmbientLight());
		polygon.setSurfaceColor(color);
		
		return polygon;
	}
}
