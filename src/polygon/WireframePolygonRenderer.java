package polygon;

import geometry.Transformation;
import line.DDALineRenderer;
import line.LineRenderer;
import shading.Shaders;
import windowing.drawable.Drawable;

public class WireframePolygonRenderer implements PolygonRenderer {
	final private static LineRenderer renderer = DDALineRenderer.make();
	private WireframePolygonRenderer() {}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shaders shaders, Clipper clipper, Transformation normalize, Transformation cameraToScreen) {
		polygon = shaders.shadeFace(polygon);
		polygon = normalize.apply(polygon);
		polygon = clipper.clip(polygon);

		if (polygon.length() < 3) return;
		
		polygon = cameraToScreen.apply(polygon);
		
		for (int i = 0; i < polygon.numVertices; i++) {

			int next = (i + 1) % polygon.numVertices;
			renderer.drawLine(polygon.get(i), polygon.get(next), drawable);
		}
	}

	public static PolygonRenderer make() {
		return new WireframePolygonRenderer();
	}
}