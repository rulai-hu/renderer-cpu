package shading;

import java.util.ArrayList;

import geometry.Point3DH;
import polygon.Polygon;
import windowing.graphics.Color;

public class ShadingStrategy {
	public class LightingData {
		private Color ambientLight;
		private ArrayList<PointLight> lights;
		private LightingData(ArrayList<PointLight> lights, Color ambientLight) {
			this.lights = lights;
			this.ambientLight = ambientLight;
		}
		
		public Color getAmbientLight() {
			return ambientLight;
		}
		
		public ArrayList<PointLight> getPointLights() {
			return lights;
		}
	}
	
	//FaceShader flatShader;
	//FaceShader ambientShader;
	private Shaders currentShader;
	private final LightingData globalData;
	
	public ShadingStrategy() {
		this.globalData = new LightingData(new ArrayList<PointLight>(), Color.BLACK);
		System.out.println("Default: ambient shader");
		ambientOnly();
		
		/*
		FaceShader ambientShader;
		FaceShader flatFaceShader;
		FaceShader nullFaceShader;
		VertexShader nullVertexShader;
		VertexShader gouraudShader;
		PixelShader flatPixelShader;
		PixelShader colorInterpolatingShader;*/
	}
	
	public Shaders getShader() {
		return currentShader;
	}
	
	public void ambientOnly() {
		currentShader = new Shaders(new AmbientShader(globalData), new NullVertexShader(), new FlatPixelShader());
	}
	
	public void flat() {
		currentShader = new Shaders(new FlatFaceShader(globalData), new NullVertexShader(), new FlatPixelShader());
		System.out.println("Switching to flat shader");
	}
	
	public void gouraud(Polygon polygon) {
		currentShader = new Shaders(new NullFaceShader(), new GouraudVertexShader(polygon), new ColorInterpolatingPixelShader());
	}
	
	public void setAmbientLight(Color ambientLight) {
		globalData.ambientLight = ambientLight;
	}

	public void registerPointLight(Point3DH pt, Color color, double A, double B) {
		globalData.lights.add(new PointLight(pt, color, A, B));
	}
}
