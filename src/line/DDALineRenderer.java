package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

public class DDALineRenderer implements LineRenderer {	
	// use the static factory make() instead of constructor.
	private DDALineRenderer() {}

	/*
	 * (non-Javadoc)
	 * @see client.LineRenderer#drawLine(client.Vertex2D, client.Vertex2D, windowing.Drawable)
	 * 
	 * @pre: p2.x >= p1.x && p2.y >= p1.y
	 */
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getX() - p1.getX();
		double deltaY = p2.getY() - p1.getY();
		double deltaZ = p2.getZ() - p1.getZ();
		
		double slope = deltaY / deltaX;
		
		double y = p1.getY();
		double z = p1.getZ();
		
		double r = p1.getColor().getIntR();
		double g = p1.getColor().getIntG();
		double b = p1.getColor().getIntB();
		
		double deltaR = p2.getColor().getIntR() - p1.getColor().getIntR();
		double deltaG = p2.getColor().getIntG() - p1.getColor().getIntG();
		double deltaB = p2.getColor().getIntB() - p1.getColor().getIntB();
		
		double slopeR = deltaR / deltaX;
		double slopeG = deltaG / deltaX;
		double slopeB = deltaB / deltaX;
		double slopeZ = deltaZ / deltaX;
		
		int color;
		
		for (int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			color = ((0xff << 24) + (((int) Math.round(r) & 0xff) << 16) + (((int) Math.round(g) & 0xff) << 8) + ((int) Math.round(b) & 0xff)); 
			drawable.setPixel(x, (int) Math.round(y), z, color);
			y = y + slope;
			r = r + slopeR;
			g = g + slopeG;
			b = b + slopeB;
			z = z + slopeZ;
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new DDALineRenderer());
	}
}
