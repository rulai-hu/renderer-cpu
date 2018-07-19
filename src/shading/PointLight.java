package shading;

import geometry.Point3DH;
import geometry.Vector3;
import windowing.graphics.Color;

public class PointLight {
	final Point3DH pos;
	final Color color;
	final double A;
	final double B;
	
	public PointLight(Point3DH pos, Color color, double A, double B) {
		this.pos = pos;
		this.color = color;
		this.A = A;
		this.B = B;
	}

	public Vector3 getIntensity() {
		return new Vector3(color.getR(), color.getG(), color.getB());
	}

	public double computeAttenuation(Point3DH surfacePt) {
		double denom = A + B * pos.displacement(surfacePt).norm();
		return 1 / denom;
	}
}