package shading;

import geometry.Vertex3D;
import polygon.Polygon;

@FunctionalInterface
public interface VertexShader extends Shader {
	public Vertex3D shade(Polygon polygon, Vertex3D vertex);

}
