package shading;

import java.util.ArrayList;

import geometry.Point3DH;
import geometry.Vector3;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public class ShadingStyle {
	public class LightingData {
		private Color ambientLight;
		private ArrayList<PointLight> lights;
		private LightingData(ArrayList<PointLight> lights, Color ambientLight) {
			this.lights = lights;
			this.ambientLight = ambientLight;
		}
		
		public Vector3 getAmbientLight() {
			return ambientLight.toVector3();
		}
		
		public ArrayList<PointLight> getPointLights() {
			return lights;
		}
	}
	
	private FaceShader currentFaceShader;
	private VertexShader currentVertexShader;
	private PixelShader currentPixelShader;
	
	private final LightingData globalData;
	
	public ShadingStyle() {
		this.globalData = new LightingData(new ArrayList<PointLight>(), Color.BLACK);
		ambientOnly();
	}
	
	public void ambientOnly() {
		currentFaceShader = new AmbientShader(globalData);
		currentVertexShader = new NullVertexShader();
		currentPixelShader = new FlatPixelShader();
	}
	
	public void flat() {
		currentFaceShader = new FlatFaceShader(globalData);
		currentVertexShader = new NullVertexShader();
		currentPixelShader = new FlatPixelShader();
	}
	
	public void gouraud() {
		currentFaceShader = new NullFaceShader();
		currentVertexShader = new GouraudVertexShader(globalData);
		currentPixelShader = new ColorInterpolatingPixelShader();
	}
	
	public void phong() {
		currentFaceShader = new NullFaceShader();
		currentVertexShader = new NullVertexShader();
		currentPixelShader = new PhongPixelShader(globalData);
	}
	
	public void setAmbientLight(Color ambientLight) {
		globalData.ambientLight = ambientLight;
	}

	public void placePointLight(Point3DH pt, Color color, double A, double B) {
		globalData.lights.add(new PointLight(pt, color, A, B));
	}

	public Polygon shadeVertices(Polygon polygon) {
		Polygon result = Polygon.makeEmpty().setSpecularData(polygon.getSpecularCoefficient(), polygon.getShininess());
		
		for (Vertex3D v : polygon.getVertexList()) {
			result.add(currentVertexShader.shade(polygon, v));
		}
		
		return result;
	}

	public Polygon shadeFace(Polygon polygon) {
		return currentFaceShader.shade(polygon);
	}
	
	public PixelShader getPixelShader() {
		return currentPixelShader;
	}

	public void precompute(Polygon polygon) {
		currentPixelShader.precompute(polygon);
	}
}
