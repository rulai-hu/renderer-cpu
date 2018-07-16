package shading;

import windowing.graphics.Color;

public class Shaders {
	FaceShader flatShader;
	FaceShader ambientShader;
	Color ambient = Color.BLACK;
	
	public Shaders() {
		flatShader = new FlatShader();
		ambientShader = new AmbientShader(ambient);
	}
	public Shader getFlatShader() {
		return flatShader;
	}
	
	public FaceShader getAmbientShader() {
		return ambientShader;
	}
	public void setAmbientLight(Color ambientLight) {
		ambient = ambientLight;
		this.ambientShader = new AmbientShader(ambientLight);
	}
}
