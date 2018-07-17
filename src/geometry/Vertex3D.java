package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	protected Point3DH point;
	private Point3DH normal;
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
	public double getCameraSpaceZ() {
		return getZ();
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
	
	public Vertex3D rounded() {
		return new Vertex3D(point.round(), color);
	}
	public Vertex3D add(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.add(other3D.getPoint()),
				            color.add(other3D.getColor()));
	}
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()));
	}
	public Vertex3D scale(double scalar) {
		return new Vertex3D(point.scale(scalar),
				            color.scale(scalar));
	}
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color);
	}
	public Vertex3D replaceColor(Color newColor) {
		return new Vertex3D(point, newColor);
	}
	public Vertex3D euclidean() {
		Point3DH euclidean = getPoint3D().euclidean();
		return replacePoint(euclidean);
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	public String toIntString() {
		return "(" + getIntX() + ", " + getIntY() + getIntZ() + ", " + ", " + getColor().toIntString() + ")";
	}
	
	public static double[] cross(Vertex3D v1, Vertex3D v2, Vertex3D v3) {
		double x1 = v1.getX();
		double y1 = v1.getY();
		double z1 = v1.getZ();
		
		double x2 = v2.getX();
		double y2 = v2.getY();
		double z2 = v2.getZ();
		
		double x3 = v3.getX();
		double y3 = v3.getY();
		double z3 = v3.getZ();

		return new double[] {
			(y2 - y1) * (z3 - z1) - (y3 - y1) * (z2 - z1),
			(z2 - z1) * (x3 - x1) - (z3 - z1) * (x2 - x1),
			(x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)
		};
	}
	
	public void setNormal(Point3DH p) {
		normal = p;
	}
	
	public Point3DH getNormal() {
		return normal;
	}
	
	public boolean hasNormal() {
		return normal != null;
	}
	public static double[] normalize(double[] v) {
		double norm = Math.sqrt((v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));
		if (norm == 0) {
			System.err.println("norm = 0");
		}
		v[0] /= norm;
		v[1] /= norm;
		v[2] /= norm;
		return v;
	}
}
