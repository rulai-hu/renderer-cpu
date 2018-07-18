package shading;

import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

public class ColorInterpolatingPixelShader implements PixelShader {
	double 	r, g, b, z,
			w1, w2, w3, 
			r1, r2, r3,
			g1, g2, g3,
			b1, b2, b3,
			z1, z2, z3;
	
	Polygon polygon;

	public void setPolygon(Polygon polygon) {		
		Vertex3D v1 = polygon.get(0);
		Vertex3D v2 = polygon.get(1);
		Vertex3D v3 = polygon.get(2);
		
		z1 = v1.getZ();
		z2 = v2.getZ();
		z3 = v3.getZ();
		
		r1 = v1.getColor().getR() / z1;
		r2 = v2.getColor().getR() / z2;
		r3 = v3.getColor().getR() / z3;
		
		g1 = v1.getColor().getG() / z1;
		g2 = v2.getColor().getG() / z2;
		g3 = v3.getColor().getG() / z3;
		
		b1 = v1.getColor().getB() / z1;
		b2 = v2.getColor().getB() / z2;
		b3 = v3.getColor().getB() / z3;
	}
	
	public void setBaryocentricCoords(double w1, double w2, double w3) {
		this.w1 = w1;
		this.w2 = w2;
		this.w3 = w3;
	}

	@Override
	public Color shade(Vertex3D current) {
		assert(polygon != null);
		
		z = current.getZ();
		
		r = w1 * r1 + w2 * r2 + w3 * r3;
		g = w1 * g1 + w2 * g2 + w3 * g3;
		b = w1 * b1 + w2 * b2 + w3 * b3;
		
		r *= z; g *= z; b *= z;
		
		return new Color(r, g, b);
	}
}
