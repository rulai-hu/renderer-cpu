package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.Point3DH;
import geometry.Vertex3D;
import line.LineRenderer;
import client.RendererTrio;
import geometry.Transformation;
import polygon.Clipper;
import polygon.Polygon;
import polygon.PolygonRenderer;
import shading.ShadingStrategy;
import windowing.drawable.DepthCueingDrawable;
import windowing.drawable.Drawable;
import windowing.drawable.ZCullingDrawable;
import windowing.graphics.Color;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	
	private Stack<Transformation> transforms = new Stack<Transformation>();
	private Transformation CTM;
	private Transformation worldToCamera;
	private Transformation cameraToScreen;
	
	private Transformation normalize;
	
	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	
	private Color defaultColor = Color.WHITE;
	private Drawable _canvas; // save the original canvas so we can go back to it
	private Drawable canvas;
	
	private LineRenderer lineRenderer;
	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	private PolygonRenderer polygonRenderer;
	
	private Clipper clipper;
	
	private ShadingStrategy shading;

	private boolean cameraLoaded = false;
	private boolean cullBackfaces = true;
	
	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	
	public SimpInterpreter(String filename, Drawable drawable, RendererTrio renderers) {
		this._canvas = new ZCullingDrawable(drawable);
		this.canvas = _canvas;
		this.clipper = new Clipper();
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.polygonRenderer = filledRenderer;
		this.shading = new ShadingStrategy();
		this.reader = new LineBasedReader(filename);
		this.readerStack = new Stack<>();
		this.CTM = Transformation.identity();
		this.worldToCamera = Transformation.identity();
	}
	
	public void interpret() {
		while (reader.hasNext()) {
			String line = reader.next().trim();
			interpretLine(line);
			while (!reader.hasNext()) {
				if (readerStack.isEmpty()) {
					return;
				} else {
					reader = readerStack.pop();
				}
			}
		}
	}
	
	public void interpretLine(String line) {
		if (!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if (tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	
	private void interpretCommand(String[] tokens) {
		switch (tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" : 	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);		break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :		interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);		break;
		case "surface" :		interpretSurface(tokens);	break;
		case "ambient" :		interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :			interpretObj(tokens);		break;
		case "flat" :		flatShading(); 				break;
		
		case "light" :		interpretLight(tokens);		break;
		
		default :
			//System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void interpretLight(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		double A = cleanNumber(tokens[4]);
		double B = cleanNumber(tokens[5]);
		
		Point3DH light = CTM.apply(new Point3DH(0, 0, 0, 1));
		
		shading.registerPointLight(light, new Color(r, g, b), A, B);
	}

	private void interpretSurface(String[] tokens) {
		this.defaultColor = interpretColor(tokens, 1);
	}

	private void interpretDepth(String[] tokens) {
		double near = cleanNumber(tokens[1]);
		double far = cleanNumber(tokens[2]);
		Color color = interpretColor(tokens, 3);
		this.canvas.clear();
		this.canvas = new DepthCueingDrawable(_canvas, near, far, color);
	}
	
	private void interpretObj(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length - 1);
		ObjReader objReader = new ObjReader(filename + ".obj", this, defaultColor);
		
		objReader.read();
		objReader.render();
	}
	
	private Transformation makeProjectedToScreenTransform(double xlow, double xhigh, double ylow, double yhigh) {
		int halfWidth = canvas.getWidth() / 2;
		int halfHeight = canvas.getHeight() / 2;
		double width = xhigh - xlow;
		double height = yhigh - ylow;
		
		double aspect = width / height;
		double s1 = halfWidth;
		double s2 = halfHeight;
		
		if (aspect > 1) {
			s2 = s2 / aspect;
		} else if (aspect < 1) {
			s1 = s1 * aspect;
		}
		
		double t1 = halfWidth;
		double t2 = halfHeight;
		
		return new Transformation(
			s1,  0,  0, t1,
			 0, s2,  0, t2,
			 0,  0,  1,  0,
			 0,  0,  0,  1
		);
	}
	
	// Takes a polygon in OCS and transforms it to VCS before rendering
	public void polygon(Polygon polygon) {
		Polygon viewPolygon = CTM.apply(polygon);

		if (cullBackfaces) {
			double[] normal = Vertex3D.cross(viewPolygon.get(0), viewPolygon.get(1), viewPolygon.get(2));
			
			double[] eye = { -viewPolygon.get(0).getX(), -viewPolygon.get(0).getY(), -viewPolygon.get(0).getZ() };
			double dot = (normal[0] * eye[0] + normal[1] * eye[1] + normal[2] * eye[2]);
	
			if (dot < 0) {
				System.out.println("culling");
				return;
			}
		}

		// Clip near and far in view space
		viewPolygon = clipper.clipZ(viewPolygon);
		
		viewPolygon = normalize.apply(viewPolygon);
		
		viewPolygon = clipper.clip(viewPolygon);

		if (viewPolygon.length() < 3) {
			return;
		}
		
		viewPolygon = transformToCamera(viewPolygon);
		
		//drawFaceNormals(viewPolygon);
		
		polygonRenderer.drawPolygon(viewPolygon, canvas, shading.getShader());
	}
	
	private void drawFaceNormals(Polygon polygon) {
		System.out.println("drawing face normal");
		Point3DH p1 = new Point3DH(0, 0, 0, 0);
		Point3DH p2;
		
		for (int i = 0; i < polygon.length(); i++) {
			p1.add(polygon.get(i).getPoint3D());
		}
		
		p1 = p1.scale(1 / polygon.length());
		
		if (polygon.hasAveragedVertexNormal()) {
			p2 = polygon.getAveragedVertexNormal();
		} else {
			p2 = polygon.getFaceNormal();
		}
		
		p2 = p1.add(p2);
		
		lineRenderer.drawLine(new Vertex3D(p1, Color.WHITE), new Vertex3D(p2, Color.WHITE), canvas);
	}
	
	private void line(Vertex3D p1, Vertex3D p2) {
		p1 = CTM.apply(p1);
		p2 = CTM.apply(p2);
		p1 = transformToCamera(p1);
		p2 = transformToCamera(p2);
		
		lineRenderer.drawLine(p1, p2, canvas);
	}
	
	private void interpretCamera(String[] tokens) {
		cameraLoaded = true;
		
		double xlow = cleanNumber(tokens[1]);
		double ylow = cleanNumber(tokens[2]);
		
		double xhigh = cleanNumber(tokens[3]);
		double yhigh = cleanNumber(tokens[4]);
		
		double near = cleanNumber(tokens[5]);
		double far = cleanNumber(tokens[6]);
			
		clipper.setZClip(near, far);
			
		// Skip the trouble of multiplying CTM by its inverse...
		CTM = Transformation.identity();
		
		// Pre-multiply everything in the stack by the inverse
		applyToTransformStack(worldToCamera);
		
		far = Math.abs(far);
		
		cameraToScreen = makeProjectedToScreenTransform(xlow, xhigh, ylow, yhigh);
		
		double s1 = 2 / (xhigh - xlow);
		double s2 = 2 / (yhigh - ylow);
		double s3 = (far + 1) / (1 - far);
		
		double z1 = (xhigh + xlow) / (xhigh - xlow);
		double z2 = (yhigh + ylow) / (yhigh - ylow);
		
		double t1 = (2 * far) / (1 - far);
		
		normalize = new Transformation(
			s1,  0, z1,  0,
			 0, s2, z2,  0,
			 0,  0, s3, t1,
			 0,  0, -1,  0 
		);
	}

	private void flatShading() {
		shading.flat();
	}
	
	private void applyToTransformStack(Transformation T) {
		for (int i = 0; i < transforms.size(); i++) {
			Transformation cameraT = T.apply(transforms.get(i));
			transforms.set(i, cameraT);
		}
	}

	private void interpretAmbient(String[] tokens) {
		Color ambientLight = new Color(
			cleanNumber(tokens[1]),
			cleanNumber(tokens[2]),
			cleanNumber(tokens[3])
		);
		
		shading.setAmbientLight(ambientLight);
	}

	private void push() {
		transforms.push(CTM);
	}
	
	private void pop() {
		CTM = transforms.pop();
	}
	
	private void wire() {
		cullBackfaces = false;
		polygonRenderer = wireframeRenderer;
	}
	
	private void filled() {
		cullBackfaces = true;
		polygonRenderer = filledRenderer;
	}
	
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		
		if (sx == 0) {
			sx = 0.000001;
		}
		
		if (sy == 0) {
			sy = 0.000001;
		}

		if (sz == 0) {
			sz = 0.000001;
		}

		CTM = CTM.apply(Transformation.scale(sx, sy, sz));
		updateCTMInverse(Transformation.scale(1/sx, 1/sy, 1/sz));
	}
	
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);

		CTM = CTM.apply(Transformation.translation(tx, ty, tz));
		updateCTMInverse(Transformation.translation(-tx, -ty, -tz));
	}
	
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angle = cleanNumber(tokens[2]);
		
		switch (axisString) {
		case "X":
			CTM = CTM.apply(Transformation.rotateX(angle));
			updateCTMInverse(Transformation.rotateX(-angle));
			break;
		case "Y": 
			CTM = CTM.apply(Transformation.rotateY(angle));
			updateCTMInverse(Transformation.rotateY(-angle));
			break;
		case "Z": 
			CTM = CTM.apply(Transformation.rotateZ(angle));
			updateCTMInverse(Transformation.rotateZ(-angle));
			break;
		default:
			System.err.println("Not a valid rotation axis");
			break;
		}
	}
	
	private void updateCTMInverse(Transformation T) {
		if (cameraLoaded) {
			return;
		}
		
		worldToCamera = T.apply(worldToCamera);
	}

	private double cleanNumber(String string) {
		return Double.parseDouble(string);
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	
	private void interpretLine(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);

		line(vertices[0], vertices[1]);
	}
	
	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);

		polygon(vertices[0], vertices[1], vertices[2]);
	}
	
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for (int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}
	
	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		
		if (colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}
		
		return new Vertex3D(point.getX(), point.getY(), point.getZ(), color);
	}
	
	public Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		
		return new Point3DH(x, y, z);
	}
	
	public Point3DH interpretNormal(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		
		return new Point3DH(x, y, z, 0);
	}
	
	public Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);
		
		return new Color(r, g, b);
	}
	
	public void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		polygon(Polygon.make(p1, p2, p3));
	}

	private Polygon transformToCamera(Polygon polygon) {
		Polygon result = Polygon.makeEmpty();
		
		for (int i = 0; i < polygon.length(); i++) {
			result.add(transformToCamera(polygon.get(i)));
		}
		
		return result;
	}
	
	private Vertex3D transformToCamera(Vertex3D vertex) {
		return cameraToScreen.apply(vertex);
	}

	public Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		
		return new Point3DH(x, y, z, w);
	}

	public Transformation getCTM() {
		return CTM;
	}
}
