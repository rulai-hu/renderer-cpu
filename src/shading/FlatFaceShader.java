package shading;

import geometry.Vertex3D;
import polygon.Polygon;

public class FlatFaceShader implements FaceShader {
	@Override
	public Polygon shade(Polygon polygon) {
		Polygon result = Polygon.makeEmpty();
		double[] normal = { 0, 0, 0 };
		boolean useVertexNormals = false;
		
		for (int i = 0; i < polygon.length(); i++) {
			if (polygon.get(i).hasNormal() == false) {
				//normal = polygon.getFaceNormal();
				break;
			} else {
				useVertexNormals = true;
				normal[0] += polygon.get(i).getNormal().getX();
				normal[1] += polygon.get(i).getNormal().getY();
				normal[2] += polygon.get(i).getNormal().getZ();
			}
		}
		
		if (useVertexNormals) {
			normal[0] /= 3;
			normal[1] /= 3;
			normal[2] /= 3;
			Vertex3D.normalize(normal);
		}
		
		return polygon;
	}
}
