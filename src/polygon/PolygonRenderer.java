package polygon;

import shading.PixelShader;
import windowing.drawable.Drawable;

public interface PolygonRenderer {
	public void drawPolygon(Polygon polygon, Drawable drawable, PixelShader pixelShader);
}
