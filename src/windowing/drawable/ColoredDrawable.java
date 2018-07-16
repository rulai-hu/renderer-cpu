package windowing.drawable;

public class ColoredDrawable extends DrawableDecorator {
	private int clearColor;
	public ColoredDrawable(Drawable delegate, int argbColor) {
		super(delegate);
		this.clearColor = argbColor;
	}
	
	@Override
	public void clear() {
		fill(this.clearColor, Double.MAX_VALUE);
	}
}
