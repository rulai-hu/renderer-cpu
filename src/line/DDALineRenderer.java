package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

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
		
		double slope = deltaY / deltaX;
		
		double z1 = p1.getZ();
		double z2 = p2.getZ();
		double deltaZ = z2 - z1;
		
		double r = p1.getColor().getR();
		double g = p1.getColor().getG();
		double b = p1.getColor().getB();
		
		double deltaR = p2.getColor().getR() - r;
		double deltaG = p2.getColor().getG() - g;
		double deltaB = p2.getColor().getB() - b;
		
		double slopeR = deltaR / deltaX;
		double slopeG = deltaG / deltaX;
		double slopeB = deltaB / deltaX;
		double slopeZ = deltaZ / deltaX;
		
		double y = p1.getY();
		double z = z1;
		
		Color color;
		
		for (int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			color = new Color(r, g, b);
			drawable.setPixel(x, (int) Math.round(y), z, color.asARGB());
			
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
