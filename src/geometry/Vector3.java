package geometry;

public class Vector3 {
	public double x, y, z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3(Vector3 v) {
		this(v.x, v.y, v.z);
	}
	
	public Vector3 add(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		
		return this;
	}
	
	public Vector3 divide(double s) {
		x /= s;
		y /= s;
		z /= s;
		
		return this;
	}
	
	public Vector3 multiply(double s) {
		x *= s;
		y *= s;
		z *= s;
		
		return this;
	}
	
	public Vector3 normalize() {
		double norm = norm();
		
		if (norm == 0) {
			System.err.println("norm = 0");
		}
		
		x /= norm;
		y /= norm;
		z /= norm;
		
		return this;
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}
	
	public static Vector3 cross(Vertex3D v1, Vertex3D v2, Vertex3D v3) {
		double x1 = v1.getX();
		double y1 = v1.getY();
		double z1 = v1.getZ();
		
		double x2 = v2.getX();
		double y2 = v2.getY();
		double z2 = v2.getZ();
		
		double x3 = v3.getX();
		double y3 = v3.getY();
		double z3 = v3.getZ();

		return new Vector3(
			(y2 - y1) * (z3 - z1) - (y3 - y1) * (z2 - z1),
			(z2 - z1) * (x3 - x1) - (z3 - z1) * (x2 - x1),
			(x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)
		);
	}

	public double dot(Vector3 v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public Vector3 hadamard(Vector3 v) {
		this.x *= v.x;
		this.y *= v.y;
		this.z *= v.z;
		return this;
	}

	public Vector3 subtract(Vector3 v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public Vector3 addScalar(double real) {
		this.x += real;
		this.y += real;
		this.z += real;
		return this;
	}

	public double norm() {
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}
}
