package polygon;

import geometry.Transformation;
import shading.Shaders;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	public void drawPolygon(Polygon polygon, Drawable drawable, Shaders shaders, Clipper clipper, Transformation normalize, Transformation cameraToScreen);
}
