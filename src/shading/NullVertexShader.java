package shading;

import geometry.Vertex3D;
import polygon.Polygon;

public class NullVertexShader implements VertexShader {

	@Override
	public Vertex3D shade(Polygon polygon, Vertex3D vertex) {
		return vertex;
	}

}
