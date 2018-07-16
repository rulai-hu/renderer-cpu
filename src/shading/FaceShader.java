package shading;

import polygon.Polygon;

@FunctionalInterface
public interface FaceShader extends Shader {
	public Polygon shade(Polygon polygon);
}
