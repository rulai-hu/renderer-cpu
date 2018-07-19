package shading;

import polygon.Polygon;
import shading.ShadingStrategy.LightingData;

public class AmbientShader implements FaceShader {
	private LightingData data;
	
	public AmbientShader(LightingData data) {
		this.data = data;
	}

	@Override
	public Polygon shade(Polygon polygon) {
		Polygon result = Polygon.makeEmpty();
		
		for (int i = 0; i < polygon.length(); i++) {
			result.add(polygon.get(i).replaceColor(data.getAmbientLight().multiply(polygon.get(i).getColor())));
		}
		
		return result;
	}
}
