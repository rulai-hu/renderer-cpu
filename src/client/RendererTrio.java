package client;

import line.DDALineRenderer;
import line.LineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireframePolygonRenderer;

public class RendererTrio {
	
	public LineRenderer getLineRenderer() {
		return DDALineRenderer.make();
	}

	public PolygonRenderer getFilledRenderer() {
		return FilledPolygonRenderer.make();
	}

	public PolygonRenderer getWireframeRenderer() {
		return WireframePolygonRenderer.make();
	}

}
