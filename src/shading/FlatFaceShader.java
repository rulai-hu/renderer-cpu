package shading;

import geometry.Point3DH;
import geometry.Vector3;
import polygon.Polygon;
import shading.ShadingStrategy.LightingData;

public class FlatFaceShader implements FaceShader {
	private LightingData data;
	
	public FlatFaceShader(LightingData data) {
		this.data = data;
	}
	
	@Override
	public Polygon shade(Polygon polygon) {
		Vector3 normal;
		
		if (polygon.hasAveragedVertexNormal()) {
			normal = polygon.getAveragedVertexNormal();
		} else {
			normal = polygon.getFaceNormal();
		}
		
		//System.out.println("Normal=" + normal);
		
		Point3DH centroid = polygon.getCentroid();
		
		double ks = polygon.getSpecularCoefficient();
		double s = polygon.getShininess();
		
		Vector3 surfaceColor = polygon.get(0).getColor().toVector3();
		Vector3 ambient = data.getAmbientLight().toVector3();
		Vector3 L;
		Vector3 R;
		Vector3 reflectedColor;
		Vector3 view = new Vector3(-centroid.getX(), -centroid.getY(), -centroid.getZ()).normalize();
		Vector3 resultColor = new Vector3(surfaceColor).hadamard(ambient);
		
		Vector3 diffuseTerm;
		Vector3 lightResult;
		double specularTerm;
		
		for (PointLight light : data.getPointLights()) {
			L = centroid.displacement(light.pos).normalize();
			R = new Vector3(normal).multiply(normal.dot(L) * 2).subtract(L).normalize();
			diffuseTerm = new Vector3(surfaceColor).multiply(Math.max(0, normal.dot(L)));
			specularTerm = ks * Math.pow(R.dot(view), s);
			reflectedColor = new Vector3(surfaceColor).hadamard(diffuseTerm.addScalar(specularTerm));
			lightResult = light.getIntensity().multiply(light.computeAttenuation(centroid)).hadamard(reflectedColor);
			
			resultColor.add(lightResult);
		}
		
		Polygon result = Polygon.makeEmpty().setSpecularData(ks, s);
		
		for (int i = 0; i < polygon.length(); i++) {
			result.add(polygon.get(i).replaceColor(resultColor));
		}
		
		return result;
	}
}
