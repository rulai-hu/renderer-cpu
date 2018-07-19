package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	protected Point3DH point;
	private Vector3 normal;
	protected Color color;
	
	public Vertex3D(Point3DH point, Color color) {
		super();
		this.point = point;
		this.color = color;
	}
	
	public Vertex3D(double x, double y, double z, Color color) {
		this(new Point3DH(x, y, z), color);
	}

	public double getX() {
		return point.getX();
	}
	
	public double getY() {
		return point.getY();
	}
	
	public double getZ() {
		return point.getZ();
	}
	
	public Point getPoint() {
		return point;
	}
	
	public Point3DH getPoint3D() {
		return point;
	}
	
	public int getIntX() {
		return (int) Math.round(getX());
	}
	
	public int getIntY() {
		return (int) Math.round(getY());
	}
	
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	
	public Color getColor() {
		return color;
	}
	
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()));
	}
	
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color).setNormal(normal);
	}
	
	public Vertex3D replaceColor(Color newColor) {
		return new Vertex3D(point, newColor).setNormal(normal);
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	
	public Vertex3D setNormal(Vector3 v) {
		normal = v;
		return this;
	}
	
	public Vector3 getNormal() {
		return normal;
	}
	
	public boolean hasNormal() {
		return normal != null;
	}

	public Vertex3D replaceColor(Vector3 v) {
		return replaceColor(new Color(v.x, v.y, v.z));
	}
}
