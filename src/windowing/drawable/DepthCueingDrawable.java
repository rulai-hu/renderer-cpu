package windowing.drawable;

import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator {
	private double nearClip;
	private double farClip;
	private Color farColor;
	
	public DepthCueingDrawable(Drawable delegate, double near, double far, Color color) {
		super(delegate);
		
		nearClip = near;
		farClip = far;
		farColor = color;
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {	
		// max depth effect
		if (z <= farClip) {
			delegate.setPixel(x, y, z, farColor.asARGB());
			return;
		}
		
		// no depth effect
		if (z >= nearClip) {
			z = nearClip;
		}
		
		double frac = (z - nearClip) / (farClip - nearClip);
		Color color = farColor.blendInto(frac, Color.fromARGB(argbColor));
		delegate.setPixel(x, y, z, color.asARGB());
	}
}
