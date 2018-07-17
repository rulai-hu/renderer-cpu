package shading;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public class Shaders {
	final private FaceShader faceShader;
	final private PixelShader pixelShader;
	
	public Shaders(FaceShader face, VertexShader vertex, PixelShader pixel) {
		this.faceShader = face;
		this.pixelShader = pixel;
	}

	public Polygon shadeFace(Polygon polygon) {
		return faceShader.shade(polygon);
	}
	
	public Color shadeVertex(Polygon polygon, Vertex3D current) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Color shadePixel(Vertex3D current) {
		return pixelShader.shade(current);
	}

	public PixelShader getPixelShader() {
		return pixelShader;
	}

	public void setPolygon(Polygon polygon) {
		this.pixelShader.setPolygon(polygon);
	}
}
