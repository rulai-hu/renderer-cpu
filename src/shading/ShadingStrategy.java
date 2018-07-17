package shading;

import java.util.ArrayList;

import geometry.Point3DH;
import polygon.Polygon;
import windowing.graphics.Color;

public class ShadingStrategy {
	//FaceShader flatShader;
	//FaceShader ambientShader;
	private Shaders currentShader;
	private Color ambient = Color.BLACK;
	
	private class PointLight {
		final Point3DH pos;
		final Color color;
		final double A;
		final double B;
		
		public PointLight(Point3DH pos, Color color, double A, double B) {
			this.pos = pos;
			this.color = color;
			this.A = A;
			this.B = B;
		}
	}
	
	private ArrayList<PointLight> pointLights = new ArrayList<PointLight>();
	
	public ShadingStrategy() {
		ambientOnly();
		FaceShader ambientShader;
		FaceShader flatFaceShader;
		FaceShader nullFaceShader;
		VertexShader nullVertexShader;
		VertexShader gouraudShader;
		PixelShader flatPixelShader;
		PixelShader colorInterpolatingShader;
	}
	
	public Shaders getShader() {
		return currentShader;
	}
	
	public void ambientOnly() {
		System.out.println("Using ambient shader");
		currentShader = new Shaders(new AmbientShader(ambient), new NullVertexShader(), new FlatPixelShader());
	}
	
	public void flat() {
		currentShader = new Shaders(new FlatFaceShader(), new NullVertexShader(), new FlatPixelShader());
	}
	
	public void gouraud(Polygon polygon) {
		currentShader = new Shaders(new NullFaceShader(), new GouraudVertexShader(polygon), new ColorInterpolatingPixelShader());
	}
	
	public void setAmbientLight(Color ambientLight) {
		ambient = ambientLight;
		ambientOnly();
	}

	public void registerPointLight(Point3DH pt, Color color, double A, double B) {
		this.pointLights.add(new PointLight(pt, color, A, B));
	}
}
