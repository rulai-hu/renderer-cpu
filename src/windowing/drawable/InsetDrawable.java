package windowing.drawable;

import geometry.Point2D;
import windowing.graphics.Dimensions;

public class InsetDrawable extends TranslatingDrawable {

	private static final int MAXIMUM_X = 650;
	private static final int MAXIMUM_Y = 650;
	private static final int MINIMUM_X = 50;
	private static final int MINIMUM_Y = 50;
	
	public InsetDrawable(Drawable delegate) {
		super(delegate, new Point2D(MINIMUM_X, MINIMUM_Y), new Dimensions(MAXIMUM_X, MAXIMUM_Y));
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		if (x >= MAXIMUM_X || y >= MAXIMUM_Y) {
			return;
		}
		
		if (x < 0 || y < 0) {
			return;
		}
		
		delegate.setPixel(transformedX(x), transformedY(y), z, argbColor);
	}
}
