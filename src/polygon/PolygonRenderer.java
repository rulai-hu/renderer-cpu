package polygon;

import shading.Shaders;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	public void drawPolygon(Polygon polygon, Drawable drawable, Shaders shaders);
}
