package client.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import geometry.Point3DH;
import geometry.Vector3;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;

class ObjReader {
	private static final char COMMENT_CHAR = '#';

	private class ObjVertex {
		private final int vertexIndex;
		private final Optional<Integer> normalIndex;
		
		public ObjVertex(int vertexIndex, Optional<Integer> textureIndex, Optional<Integer> normalIndex) {
			this.vertexIndex = vertexIndex;
			this.normalIndex = normalIndex;
		}
		
		public int getVertexIndex() {
			return this.vertexIndex;
		}
		
		public Optional<Integer> getNormalIndex() {
			return this.normalIndex;
		}
	}

	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
		
		private Polygon toPolygon() {
			Polygon result = Polygon.makeEmpty();
			int vertexIdx;
			Optional<Integer> normalIdx;
			Vertex3D vertex;
			for (int i = 0; i < this.size(); i++) {
				vertexIdx = this.get(i).getVertexIndex();
				normalIdx = this.get(i).getNormalIndex();
				vertex = objVertices.get(vertexIdx);
				
				if (normalIdx.isPresent()) {
					vertex.setNormal(objNormals.get(normalIdx.get()));
				}
				
				result.add(vertex);
			}
			
			return result;
		}
	}
	
	private LineBasedReader reader;
	
	private List<Vertex3D> objVertices;
	//private List<Vertex3D> transformedVertices;
	private List<Vector3> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;
	
	private SimpInterpreter simpInterpreter;
	
	ObjReader(String filename, SimpInterpreter interpreter, Color defaultColor) {
		this.reader = new LineBasedReader(filename);
		this.simpInterpreter = interpreter;
		this.objVertices = new ArrayList<Vertex3D>();
		//this.transformedVertices = new ArrayList<Vertex3D>();
		this.objNormals = new ArrayList<Vector3>();
		this.objFaces = new ArrayList<ObjFace>();
		this.defaultColor = defaultColor;
	}

	public void render() {
		for (ObjFace face: objFaces) {
			simpInterpreter.polygon(face.toPolygon());
		}
	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	
	private void interpretObjLine(String line) {
		if (!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if (tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			try {
				interpretObjNormal(tokens);
			} catch (BadObjFileException e) {
				e.printStackTrace();
			}
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	
	private void interpretObjFace(String[] tokens) {
		if (tokens.length < 4) {
			return;
		}
		
		ObjFace face = new ObjFace();
		
		for (int i = 1; i < tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size()).get();
			Optional<Integer> normalIndex  = objIndex(subtokens, 2, objNormals.size());
			ObjVertex vertex = new ObjVertex(vertexIndex, Optional.of(0), normalIndex);
			
			face.add(vertex);
		}
		
		objFaces.add(face);
	}

	private Optional<Integer> objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		if (tokenIndex >= subtokens.length) {
			return Optional.empty();
		}
		
		String token = subtokens[tokenIndex];
		
		if (token.isEmpty()) {
			return Optional.empty();
		}
		
		int index = Integer.parseInt(token);
		
		if (index > 0) {
			index--;
		}
		
		assert(baseForNegativeIndices > 0);
		
		while (index < 0) {
			index += baseForNegativeIndices;
		}
		
		return Optional.of(index);
	}

	private void interpretObjNormal(String[] tokens) throws BadObjFileException {
		int numArgs = tokens.length - 1;
		if (numArgs != 3) {
			throw new BadObjFileException("Vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);				
		}
	
		Vector3 normal = simpInterpreter.interpretNormal(tokens, 1);
		objNormals.add(normal);
	}
	
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;

		try {
			Point3DH point = objVertexPoint(tokens, numArgs);
			Color colour = objVertexColor(tokens, numArgs);
			Vertex3D vertex = new Vertex3D(point, colour);
			objVertices.add(vertex);
		} catch (BadObjFileException e) {
			e.printStackTrace();
		}
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if (numArgs == 6) {
			return simpInterpreter.interpretColor(tokens, 4);
		}
		
		if (numArgs == 7) {
			return simpInterpreter.interpretColor(tokens, 5);
		}
		
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) throws BadObjFileException {
		if (numArgs == 3 || numArgs == 6) {
			return simpInterpreter.interpretPoint(tokens, 1);
		}
		
		else if (numArgs == 4 || numArgs == 7) {
			return simpInterpreter.interpretPointWithW(tokens, 1);
		}
		
		throw new BadObjFileException("Vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}