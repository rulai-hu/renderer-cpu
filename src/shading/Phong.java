package shading;

import geometry.Point3DH;
import geometry.Vector3;
import shading.ShadingStyle.LightingData;

public class Phong {
	public static Vector3 computeColor(LightingData data, Vector3 normal, Point3DH surfacePoint, Vector3 surfaceColor, double ks, double s) {
		Vector3 result = new Vector3(surfaceColor).hadamard(data.getAmbientLight());
		
		if (result.x >= 1 && result.y >= 1 && result.z >=1) return result.clamp(1);
		
		Vector3 view = new Vector3(-surfacePoint.getX(), -surfacePoint.getY(), -surfacePoint.getZ()).normalize();
		
		view.normalize();
		
		Vector3 L;
		Vector3 R;
		Vector3 reflectedColor;
		Vector3 diffuseTerm;
		normal = new Vector3(normal).normalize();
		double specularTerm;
		double normalDotL;

		for (PointLight light : data.getPointLights()) {
			L = surfacePoint.displacement(light.pos).normalize();
			
			normalDotL = normal.dot(L);
			
			if (normalDotL < 0) {
				continue;
			}

			R = new Vector3(normal).multiply(normalDotL * 2).subtract(L).normalize();
			diffuseTerm = new Vector3(surfaceColor).multiply(Math.max(0, normalDotL));
			specularTerm = ks * Math.pow(Math.max(0, R.dot(view)), s);
			reflectedColor = new Vector3(surfaceColor).hadamard(diffuseTerm).addScalar(specularTerm);
			
			result.add(light.getIntensity().multiply(light.computeAttenuation(surfacePoint)).hadamard(reflectedColor));
		}
		
		//System.out.println("Result:" + result);

		return result.clamp(1);
	}
}
