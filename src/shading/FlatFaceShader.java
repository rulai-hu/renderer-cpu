package shading;

import geometry.Point3DH;
import geometry.Vector3;
import polygon.Polygon;
import shading.ShadingStyle.LightingData;
import windowing.graphics.Color;

public class FlatFaceShader implements FaceShader {
	private LightingData data;
	
	public FlatFaceShader(LightingData data) {
		this.data = data;
	}
	
	@Override
	public Polygon shade(Polygon polygon) {
		Vector3 normal;
		
		if (polygon.hasAveragedVertexNormal()) {
			normal = polygon.getAveragedVertexNormal();
		} else {
			normal = polygon.getFaceNormal();
		}

		Point3DH centroid = polygon.getCentroid();

		double ks = polygon.getSpecularCoefficient();
		double s = polygon.getShininess();
		Vector3 surfaceColor = polygon.get(0).getColor().toVector3();
		
		Vector3 resultColor = Phong.computeColor(data, normal, centroid, surfaceColor, ks, s);

		return polygon.setSurfaceColor(new Color(resultColor));
	}
}
