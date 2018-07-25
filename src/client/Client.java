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
	
	private static final int NUM_PAGES = 17;
	
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
		case 1:  filename = "page-a1";	break;
		case 2:  filename = "page-a2";	break;
		case 3:	 filename = "page-a3";	break;
		case 4:  filename = "page-b1";	break;
		case 5:  filename = "page-b2";	break;
		case 6:  filename = "page-b3";	break;
		case 7:  filename = "page-c1";	break;
		case 8:  filename = "page-c2";	break;
		case 9:  filename = "page-c3";	break;
		case 10: filename = "page-d";	break;
		case 11: filename = "page-e";	break;
		case 12: filename = "page-f1";	break;
		case 13: filename = "page-f2";	break;
		case 14: filename = "page-g";	break;
		case 15: filename = "page-h";	break;
		case 16: filename = "page-i";	break;

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
