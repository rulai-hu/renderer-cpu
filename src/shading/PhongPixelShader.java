package shading;

import geometry.Point3DH;
import geometry.Vector3;
import geometry.Vertex3D;
import polygon.Polygon;
import shading.ShadingStyle.LightingData;
import windowing.graphics.Color;

public class PhongPixelShader extends ColorInterpolatingPixelShader {
	private LightingData data;
	private Vertex3D v1, v2, v3;
	private Point3DH cs1, cs2, cs3;
	private boolean useVertexNormals = false;
	private Vector3 faceNormal;
	
	public PhongPixelShader(LightingData data) {
		this.data = data;
	}
	
	@Override
	public Color shade(Polygon polygon, double z, double w1, double w2, double w3) {
		Color interpolatedColor = super.shade(polygon, z, w1, w2, w3);
		
		Vector3 normal;
		
		if (useVertexNormals) {
			normal = new Vector3(v1.getCameraSpaceNormal()).multiply(w1)
				.add(new Vector3(v2.getCameraSpaceNormal()).multiply(w2))
				.add(new Vector3(v3.getCameraSpaceNormal()).multiply(w3))
				.normalize();
		} else {
			normal = faceNormal;
		}
		
		Point3DH point = cs1.scale(w1).add(cs2.scale(w2)).add(cs3.scale(w3));
		
		Color result = new Color(
				Phong.computeColor(
						data, normal, point, interpolatedColor.toVector3(),
						polygon.getSpecularCoefficient(), polygon.getShininess()
				)
			);
		
		return result;
	}

	@Override
	public void precompute(Polygon polygon) {
		super.precompute(polygon);
		
		v1 = polygon.get(0);
		v2 = polygon.get(1);
		v3 = polygon.get(2);
		
		cs1 = v1.getCameraSpacePoint();
		cs2 = v2.getCameraSpacePoint();
		cs3 = v3.getCameraSpacePoint();
		useVertexNormals = v1.hasNormal() && v2.hasNormal() && v3.hasNormal();
		
		if (!useVertexNormals) {
			faceNormal = polygon.getFaceNormal();
			
			if (new Vector3(-cs1.getX(), -cs1.getY(), -cs1.getZ()).dot(faceNormal) < 0) {
				faceNormal.multiply(-1);
			}
		}
	}
}
