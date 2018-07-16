package client;

import java.util.List;

import client.interpreter.SimpInterpreter;

import windowing.PageTurner;
import windowing.drawable.ColoredDrawable;
import windowing.drawable.Drawable;
import windowing.drawable.InsetDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.graphics.Dimensions;

import geometry.Point2D;

public class Client implements PageTurner {
	//private static final int ARGB_BLACK = 0x00_00_00_00;
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;
	
	private static final int NUM_PAGES = 10;
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable canvas;
	private SimpInterpreter interpreter;
	
	String filename;
	boolean hasArgument = false;
	
	public Client(Drawable drawable, List<String> args) {
		if (args.size() > 0) {
			hasArgument = true;
			this.filename = args.get(0);
		}
		
		this.drawable = drawable;	
		createDrawable();
	}

	public void createDrawable() {
		canvas = new InvertedYDrawable(drawable);
		canvas = new TranslatingDrawable(canvas, point(0, 0), dimensions(750, 750));
		canvas = new ColoredDrawable(canvas, ARGB_WHITE);
		canvas.clear();
		canvas = new InsetDrawable(canvas);
	}
	
	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}
	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	
	@Override
	public void nextPage() {
		if(hasArgument) {
			argumentNextPage();
		}
		
		else {
			noArgumentNextPage();
		}
	}

	private void argumentNextPage() {
		canvas.clear();
		interpreter = new SimpInterpreter(filename + ".simp", canvas, new RendererTrio());
		interpreter.interpret();
	}
	
	public void noArgumentNextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		canvas.clear();

		String filename;

		switch(pageNumber) {
		case 1:  filename = "pageA";	 break;
		case 2:  filename = "pageB";	 break;
		case 3:	 filename = "pageC";	 break;
		case 4:  filename = "pageD";	 break;
		case 5:  filename = "pageE";	 break;
		case 6:  filename = "pageF";	 break;
		case 7:  filename = "pageG";	 break;
		case 8:  filename = "pageH";	 break;
		case 9:  filename = "pageI";	 break;
		case 0:  filename = "pageJ";	 break;

		default: defaultPage();
				 return;
		}
	
		interpreter = new SimpInterpreter(filename + ".simp", canvas, new RendererTrio());
		interpreter.interpret();
	}

	private void defaultPage() {
		canvas.clear();
		canvas.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
