package shading;

import geometry.Vector3;
import geometry.Vertex3D;
import polygon.Polygon;
import shading.ShadingStyle.LightingData;

public class GouraudVertexShader implements VertexShader {
	private LightingData data;
	
	public GouraudVertexShader(LightingData data) {
		this.data = data;
	}

	@Override
	public Vertex3D shade(Polygon polygon, Vertex3D vertex) {
		Vector3 normal;
		
		if (vertex.hasNormal()) {
			normal = vertex.getNormal();
		} else {
			normal = polygon.getFaceNormal();
		}
		
		Vector3 color = Phong.computeColor(data, normal, vertex.getPoint3D(), vertex.getColor().toVector3(),
				polygon.getSpecularCoefficient(), polygon.getShininess());
		
		return vertex.replaceColor(color);
	}

}
