package shading;

import polygon.Polygon;

public class NullFaceShader implements FaceShader {

	@Override
	public Polygon shade(Polygon polygon) {
		return polygon;
	}

}
