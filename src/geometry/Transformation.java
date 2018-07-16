package geometry;

import polygon.Polygon;

public class Transformation {
	private double[][] matrix = new double[4][4];
	public Transformation(double... entries) {
		if (entries.length != 16) {
			System.err.println("Bad matrix input, needs exactly 16 entries");
		}
		
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				matrix[row][col] = entries[(row * 4) + col];
			}
		}
	}
	
	public static Transformation identity() {
		return new Transformation(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		);
	}

	public static Transformation translation(double tx, double ty, double tz) {
		return new Transformation(
			 1,  0,  0, tx,
			 0,  1,  0, ty,
			 0,  0,  1, tz,
			 0,  0,  0,  1
		);
	}

	public static Transformation scale(double sx, double sy, double sz) {
		return new Transformation(
			sx,  0,  0,  0,
			 0, sy,  0,  0,
			 0,  0, sz,  0,
			 0,  0,  0,  1
		);
	}

	// Apply is postmultiplication of current transformation with T ie. C * T
	public Transformation apply(Transformation T) {
		return Transformation.from2DArray(
			mult(matrix, T.matrix)
		);
	}

	private static Transformation from2DArray(double[][] M) {
		return new Transformation(
			M[0][0], M[0][1], M[0][2], M[0][3],
			M[1][0], M[1][1], M[1][2], M[1][3],
			M[2][0], M[2][1], M[2][2], M[2][3], 
			M[3][0], M[3][1], M[3][2], M[3][3]
		);
	}

	// def not optimized...
	private double[][] mult(double[][] M1, double[][] M2) {
		double[][] result = new double[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				double[] v1 = { M1[i][0], M1[i][1], M1[i][2], M1[i][3] };
				double[] v2 = { M2[0][j], M2[1][j], M2[2][j], M2[3][j] };
				result[i][j] = v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2] + v1[3] * v2[3];
			}
		}
		
		return result;
	}

	private double[] apply(double x, double y, double z, double w) {
		double[] result = { x, y, z, w };
		
		for (int row = 0; row < 4; row++) {
			double a = matrix[row][0];
			double b = matrix[row][1];
			double c = matrix[row][2];
			double d = matrix[row][3];
			
			result[row] = x * a + y * b + z * c + w * d;
		}
		
		return result;
	}
	
	public Vertex3D apply(Vertex3D vertex) {
		Point3DH p = apply(vertex.getPoint3D());
		return vertex.replacePoint(p);
	}
	
	public Polygon apply(Polygon polygon) {
		Polygon result = Polygon.makeEmpty();
		
		for (int i = 0; i < polygon.length(); i++) {
			Point3DH p = apply(polygon.get(i).getPoint3D());
			//System.out.println("Mapping point " + polygon.get(i).getPoint3D());
			//System.out.println("Mapped to: " + p);
			result.add(polygon.get(i).replacePoint(p));
		}

		return result;
	}

	public Point3DH apply(Point3DH p) {
		double[] result = apply(p.getX(), p.getY(), p.getZ(), p.getW());
		return new Point3DH(result[0], result[1], result[2], result[3]);
	}
	
	public String toString() {
		String result = "\n";
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result += matrix[i][j] + " ";
			}
			result += "\n";
		}
		
		return result;
	}
	
	public static Transformation rotateX(double deg) {
		double a = 2 * Math.PI * deg / 360;
		double c = Math.cos(a);
		double s = Math.sin(a);
		return new Transformation(
			 1,  0,  0,  0,
			 0,  c, -s,  0,
			 0,  s,  c,  0,
			 0,  0,  0,  1
		);
	}
	
	public static Transformation rotateY(double deg) {
		double a = 2 * Math.PI * deg / 360;
		double c = Math.cos(a);
		double s = Math.sin(a);
		return new Transformation(
			 c,  0,  s,  0,
			 0,  1,  0,  0,
			-s,  0,  c,  0,
			 0,  0,  0,  1
		);
	}
	
	public static Transformation rotateZ(double deg) {
		double a = 2 * Math.PI * deg / 360;
		double c = Math.cos(a);
		double s = Math.sin(a);
		return new Transformation(
			 c, -s,  0,  0,
			 s,  c,  0,  0,
			 0,  0,  1,  0,
			 0,  0,  0,  1
		);
	}
}
