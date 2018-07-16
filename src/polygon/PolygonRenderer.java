package polygon;

import shading.FaceShader;
import shading.VertexShader;
import shading.PixelShader;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	// assumes polygon is ccw.
	public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader shader);
	public void drawPolygon(Polygon polygon, Drawable drawable, VertexShader shader);
	public void drawPolygon(Polygon polygon, Drawable drawable, PixelShader shader);
}
