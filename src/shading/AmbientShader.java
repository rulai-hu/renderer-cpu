package shading;

import polygon.Polygon;
import windowing.graphics.Color;

public class AmbientShader implements FaceShader {
	public Color ambientLight;
	
	public AmbientShader(Color ambient) {
		ambientLight = ambient;
	}

	@Override
	public Polygon shade(Polygon polygon) {
		Polygon result = Polygon.makeEmpty();
		
		for (int i = 0; i < polygon.length(); i++) {
			result.add(polygon.get(i).replaceColor(ambientLight.multiply(polygon.get(i).getColor())));
		}
		
		return result;
	}
}
