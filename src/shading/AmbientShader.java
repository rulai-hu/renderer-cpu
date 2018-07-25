package shading;

import geometry.Vertex3D;
import polygon.Polygon;
import shading.ShadingStyle.LightingData;
import windowing.graphics.Color;

public class AmbientShader implements VertexShader {
	private LightingData data;
	
	public AmbientShader(LightingData data) {
		this.data = data;
	}

	@Override
	public Vertex3D shade(Polygon polygon, Vertex3D vertex) {
		Color color = vertex.getColor().multiply(data.getAmbientLight());
		
		return vertex.replaceColor(color);
	}
}
