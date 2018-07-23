package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	private final static Point3DH NO_SAVE = new Point3DH(0, 0, 0, 0);
	private final static Vector3 NO_NORMAL = new Vector3(0, 0, 0);
	protected Point3DH point;
	private Vector3 normal = NO_NORMAL;
	protected Color color;
	private Point3DH cameraSpacePt = NO_SAVE;
	private Vector3 cameraSpaceNormal;
	
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
		Vertex3D other3D = (Vertex3D) other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()))
				.setCameraSpaceData(cameraSpacePt, cameraSpaceNormal);
	}
	
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color)
				.setNormal(normal)
				.setCameraSpaceData(cameraSpacePt, cameraSpaceNormal);
	}
	
	public Vertex3D replaceColor(Color newColor) {
		return new Vertex3D(point, newColor)
				.setNormal(normal)
				.setCameraSpaceData(cameraSpacePt, cameraSpaceNormal);
	}
	
	private Vertex3D setCameraSpaceData(Point3DH pt, Vector3 v) {
		cameraSpacePt = new Point3DH(pt.getX(), pt.getY(), pt.getZ(), pt.getW());
		if (v != null) {
			cameraSpaceNormal = new Vector3(v);
		}
		
		return this;
	}

	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	
	public Vertex3D setNormal(Vector3 v) {
		if (v != NO_NORMAL) {
			v.normalize();
		}
		normal = v;
		return this;
	}
	
	public Vector3 getNormal() {
		return normal;
	}
	
	public boolean hasNormal() {
		return normal != NO_NORMAL;
	}

	public Vertex3D replaceColor(Vector3 v) {
		return replaceColor(new Color(v.x, v.y, v.z));
	}
	
	public Point3DH getCameraSpacePoint() {
		return cameraSpacePt;
	}
	
	public Vector3 getCameraSpaceNormal() {
		return cameraSpaceNormal;
	}

	public Vertex3D saveCameraSpaceData() {
		return this.setCameraSpaceData(point, normal);
	}
}
