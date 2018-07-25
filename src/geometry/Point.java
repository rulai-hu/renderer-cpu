package geometry;

public interface Point {
	public double getX();
	public double getY();
	
	public Point round();
	public Point add(Point other);
	public Point subtract(Point other);
	public Point scale(double scale);
}
