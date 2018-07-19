package geometry;

import windowing.graphics.Color;

public interface Vertex {
	public Vertex subtract(Vertex other);
	public int getIntX();
	public int getIntY();
	public double getX();
	public double getY();
	public Color getColor();
	public Point getPoint();
}
