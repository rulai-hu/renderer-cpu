package polygon;

import line.DDALineRenderer;
import line.LineRenderer;
import shading.FaceShader;
import shading.PixelShader;
import shading.VertexShader;
import windowing.drawable.Drawable;

public class WireframePolygonRenderer implements PolygonRenderer {
	private WireframePolygonRenderer() {}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader shader) {
		LineRenderer renderer = DDALineRenderer.make();
		
		//polygon = shader.shade(polygon);
		
		for (int i = 0; i < polygon.numVertices; i++) {

			int next = (i + 1) % polygon.numVertices;
			renderer.drawLine(polygon.get(i), polygon.get(next), drawable);
		}
	}

	public static PolygonRenderer make() {
		return new WireframePolygonRenderer();
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, VertexShader shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, PixelShader shader) {
		// TODO Auto-generated method stub
		
	}
}