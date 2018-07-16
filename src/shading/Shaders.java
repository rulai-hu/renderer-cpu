package shading;

public class Shaders {
	FaceShader flatShader;
	public Shaders() {
		flatShader = new FlatShader();
	}
	public Shader getFlatShader() {
		return flatShader;
	}
}
