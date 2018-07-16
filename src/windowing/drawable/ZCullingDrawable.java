package windowing.drawable;

public class ZCullingDrawable extends DrawableDecorator {
	public double[][] buffer = new double[650][650];
	int size = buffer.length;
	
	public ZCullingDrawable(Drawable delegate) {
		super(delegate);
		initializeBuffer();
	}
	
	private void initializeBuffer() {		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				buffer[i][j] = Double.NEGATIVE_INFINITY;
			}
		}
	}
	
	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		if (x >= size || y >= size || x < 0 || y < 0) {
			return;
		}
		
		if (z <= buffer[y][x]) {
			return;
		}
		
		buffer[y][x] = z;
		delegate.setPixel(x, y, z, argbColor);
	}
}
