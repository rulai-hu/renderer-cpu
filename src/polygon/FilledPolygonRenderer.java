package polygon;

import java.util.ArrayList;

import geometry.Vertex3D;
import shading.PixelShader;
import shading.VertexShader;
import shading.FaceShader;

import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class FilledPolygonRenderer implements PolygonRenderer {
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader shader) {
		if (polygon.numVertices < 3) return;
		if (polygon.numVertices > 3) {
			for (Polygon tri : triangulate(polygon)) {
				draw(shader.shade(tri), drawable);
			}
			
			return;
		}

		draw(shader.shade(polygon), drawable);
	}

	private void draw(Polygon polygon, Drawable drawable) {
		Chain left = polygon.leftChain();
		Chain right = polygon.rightChain();
		
		Chain longest;
		
		if (left.length() > right.length()) {
			longest = left;
		} else {
			longest = right;
		}
		
		Vertex3D v1 = longest.get(0);
		Vertex3D v2 = longest.get(1);
		Vertex3D v3 = longest.get(2);
		
		int maxY = v1.getIntY();
		int minX = v3.getIntX();
		int minY = v3.getIntY();
		
		int midpointY = v2.getIntY();
		int midpointX = v2.getIntX();

		double leftX = left.get(0).getIntX();
		double rightX = leftX;
		
		double leftSlopes = slope(left.get(0), left.get(1));
		double rightSlopes = slope(right.get(0), right.get(1));
		
		double leftSlope = leftSlopes;
		double rightSlope = rightSlopes;
		
		// slope from midpoint to last vertex of long chain
		double nextSlopes = slope(v2, v3);
		
		// Factors for computing barycentric weights W1, W2, W3
		double f1 = v2.getY() - v3.getY();
		double f2; // depends on current point
		double f3 = v3.getX() - v2.getX();
		double f4; // depends on current point
		double f5 = v1.getX() - v3.getX();
		double f6 = v1.getY() - v3.getY();
		double f7 = v3.getY() - v1.getY();
		
		double v1Red = v1.getColor().getR() / v1.getZ();
		double v2Red = v2.getColor().getR() / v2.getZ();
		double v3Red = v3.getColor().getR() / v3.getZ();
		
		double v1Green = v1.getColor().getG() / v1.getZ();
		double v2Green = v2.getColor().getG() / v2.getZ();
		double v3Green = v3.getColor().getG() / v3.getZ();
		
		double v1Blue = v1.getColor().getB() / v1.getZ();
		double v2Blue = v2.getColor().getB() / v2.getZ();
		double v3Blue = v3.getColor().getB() / v3.getZ();
		
		double v1Z = 1 / v1.getZ();
		double v2Z = 1 / v2.getZ();
		double v3Z = 1 / v3.getZ();
		
		for (int currY = maxY; currY > minY; --currY) {
			if (currY == midpointY) {
				if (longest == left) {
					leftSlope = nextSlopes;
					leftX = midpointX;
				} else {
					rightSlope = nextSlopes; 
					rightX = midpointX;
				}
			}
			
			leftX = leftX - leftSlope;
			rightX = rightX - rightSlope;
			
			int leftBound = (int) Math.round(leftX);
			int rightBound = (int) Math.round(rightX);
			
			double r, g, b;
			double w1, w2, w3, z;

			for (int i = leftBound; i < rightBound; ++i) {
				f2 = i - minX;
				f4 = currY - minY;
				
				w1 = (f1 * f2 + f3 * f4) /
					 (f1 * f5 + f3 * f6);
				w2 = (f7 * f2 + f5 * f4) /
					 (f1 * f5 + f3 * f6);
				w3 = 1 - w2 - w1;
				
				r = w1 * v1Red + w2 * v2Red + w3 * v3Red;
				g = w1 * v1Green + w2 * v2Green + w3 * v3Green;
				b = w1 * v1Blue + w2 * v2Blue + w3 * v3Blue;
				
				z = 1 / (w1 * v1Z + w2 * v2Z + w3 * v3Z);
				
				r *= z; g *= z; b *= z;

				drawable.setPixel(i, currY, z, new Color(r, g, b).asARGB());
			}
		}
	}

	private double slope(Vertex3D v1, Vertex3D v2) {
		double dx;
		double dy;
		
		dx = v2.getIntX() - v1.getIntX();
		dy = v2.getIntY() - v1.getIntY();
		
		return dx / dy;
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, VertexShader shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, PixelShader shader) {
		// TODO Auto-generated method stub
		
	}
	
	public static PolygonRenderer make() {
		return new FilledPolygonRenderer();
	}
	
	private ArrayList<Polygon> triangulate(Polygon polygon) {
		ArrayList<Polygon> result = new ArrayList<Polygon>();
		
		for (int i = 2; i < polygon.numVertices; i++) {
			Polygon tri = Polygon.make(
				polygon.get(0),
				polygon.get(i - 1),
				polygon.get(i)
			);

			result.add(tri);
		}
		
		return result;
	}
	
	private FilledPolygonRenderer() {}
}