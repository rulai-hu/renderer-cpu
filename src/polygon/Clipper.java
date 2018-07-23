package polygon;

import geometry.Point3DH;
import geometry.Vertex3D;
import windowing.graphics.Color;

public class Clipper {
	private double nearClip = Double.POSITIVE_INFINITY;
	private double farClip = Double.NEGATIVE_INFINITY;

	private final double[][] normals;
	
	public Clipper() {
		normals = new double[][] {
			new double[] {1, 0, 0}, // left
			new double[] {0, -1, 0}, // top
			new double[] {-1, 0, 0}, // right
			new double[] {0, 1, 0}, // bottom
			new double[] {0, 0, 1}, // near
			new double[] {0, 0, -1}  // far, wherever you are
		};
	}
	
	public Polygon clipZ(Polygon polygon) {
		double specCoeff = polygon.getSpecularCoefficient();
		double shininess = polygon.getShininess();
		Color surfaceColor = polygon.getSurfaceColor();
		
		polygon = clip(polygon, new double[] {0, 0, -1}, -nearClip + 0.0001, false);
		return clip(polygon, new double[] {0, 0, 1}, farClip + 0.0001, false)
				.setSpecularData(specCoeff, shininess)
				.setSurfaceColor(surfaceColor);
	}
	
	public Polygon clip(Polygon polygon) {
		Color surfaceColor = polygon.getSurfaceColor();
		double ks = polygon.getSpecularCoefficient();
		double s = polygon.getShininess();
		
		Polygon result = Polygon.makeEmpty();
		
		Point3DH p;
		double x, y, w;
		
		for (Vertex3D v : polygon.getVertexList()) {
			p = v.getPoint3D();

			w = p.getW();
			x = p.getX();
			y = p.getY();
			
			result.add(v.replacePoint(new Point3DH(x/w, y/w, -w)));
		}

		result = clip(result, normals[0], -1, true);
		result = clip(result, normals[1], -1, true);
		result = clip(result, normals[2], -1, true);
		result = clip(result, normals[3], -1, true);
		
		return result.setSpecularData(ks, s).setSurfaceColor(surfaceColor);
	}
	
	private Polygon clip(Polygon polygon, double[] normal, double clipDistance, boolean perspCorrect) {
		Polygon result = Polygon.makeEmpty();
		
		if (polygon.numVertices < 3) {
			return result;
		}
		
		Vertex3D currentVertex = polygon.get(0);
		Vertex3D nextVertex;
		Vertex3D newVertex;
		
		double currentDot = dot(currentVertex.getPoint3D(), normal);
		double nextDot;
		
		boolean currentVertexIsIn = currentDot >= clipDistance;
		boolean nextVertexIsIn;
		
		double interp, x, y, z, currentZ, nextZ;

		for (int i = 0; i < polygon.numVertices; i++) {
			nextVertex = polygon.get((i + 1) % polygon.numVertices);
			nextDot = dot(nextVertex.getPoint3D(), normal);
			nextVertexIsIn = nextDot >= clipDistance;
			
			if (currentVertexIsIn) {
				result.add(currentVertex);
			}

			if (currentVertexIsIn != nextVertexIsIn) {
				if (perspCorrect) {
					currentZ = 1 / currentVertex.getZ();
					nextZ = 1 / nextVertex.getZ();
				} else {
					currentZ = currentVertex.getZ();
					nextZ = nextVertex.getZ();
				}
				
				interp = (clipDistance - currentDot) / (nextDot - currentDot);
				
				x = currentVertex.getX() + ((nextVertex.getX() - currentVertex.getX()) * interp);
				y = currentVertex.getY() + ((nextVertex.getY() - currentVertex.getY()) * interp);
				z = currentZ + ((nextZ - currentZ) * interp);

				if (perspCorrect) z = 1 / z;

				newVertex = new Vertex3D(x, y, z, nextVertex.getColor());

				result.add(newVertex);
			}
			
			currentDot = nextDot;
			currentVertexIsIn = nextVertexIsIn;
			currentVertex = nextVertex;
		}
		
		return result;
	}
	
	public void setZClip(double near, double far) {
		nearClip = near;
		farClip = far;
	}
	
	private double dot(Point3DH v1, double[] v2) {
		return v1.getX() * v2[0] + v1.getY() * v2[1] + v1.getZ() * v2[2];
	}
}
