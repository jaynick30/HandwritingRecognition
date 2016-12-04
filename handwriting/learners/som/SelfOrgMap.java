package handwriting.learners.som;

import handwriting.core.Drawing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SelfOrgMap {
	private int drawingWidth, drawingHeight, width, height;
	public Drawing[][] drawings;
	private double radius;
	private double rate;
	
	// Representation data type
	
	public SelfOrgMap(int width, int height, int dWidth, int dHeight, double rate, double radius) {
		this.drawingWidth = dWidth;
		this.drawingHeight = dHeight;
		this.width = width;
		this.height = height;
		this.rate = rate;
		this.radius = radius;
		this.drawings = new Drawing[width][height];
		populateDrawings();
		/* TODO: Initialize your representation here */
	}
	
	private void populateDrawings(){
		for (int x = 0; x < getWidth(); x++) { 
			for (int y = 0; y < getHeight(); y++) {
				drawings[x][y] = new Drawing(40,40);
				for (int x1 = 0; x1 < getDrawingWidth(); x1++) {
					for (int y1 = 0; y1 < getDrawingHeight(); y1++) {
						double val = Math.random();
						if(val >= 0.5){
							drawings[x][y].set(x1, y1, true);
						}
						else{
							drawings[x][y].set(x1, y1, false);
						}
					}
				}
			}
		}
	}
	
	
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	public int getDrawingWidth() {return drawingWidth;}
	public int getDrawingHeight() {return drawingHeight;}
	
	public SOMPoint bestFor(Drawing example) {
		int sum = 0;
		SOMPoint bestDrawing = new SOMPoint(0,0);
		int bestDistance = 1600;
		for (int x = 0; x < getWidth(); x++) { 
			for (int y = 0; y < getHeight(); y++) {
				SOMPoint cell = new SOMPoint(x, y);
				sum = getDistance(drawings[x][y], example);
				if(sum < bestDistance){
					bestDistance = sum;
					bestDrawing = cell;
				}
			}
		}
		return bestDrawing;
	}
	
	public int getDistance(Drawing drawing1, Drawing drawing2){
		int sum = 0;
		for (int x1 = 0; x1 < getDrawingWidth(); x1++) {
			for (int y1 = 0; y1 < getDrawingHeight(); y1++) {
				if((!drawing1.isSet(x1, y1) && drawing2.isSet(x1, y1)) || (drawing1.isSet(x1, y1) && !drawing2.isSet(x1, y1))){
					//System.out.print("drawing1 is set: " + drawing1.isSet(x1, y1));
					//System.out.println(" , drawing2 is set " + drawing2.isSet(x1, y1));
					sum += 1;
				}
			}
		}
		return sum;
		
		
		
	}
	
	public boolean isLegal(SOMPoint point) {
		return point.x() >= 0 && point.x() < getWidth() && point.y() >= 0 && point.y() < getHeight();
	}
	
	public void train(Drawing example) {
		double radius = this.radius;
		double rate = this.rate;
		SOMPoint centralSOM = bestFor(example);
		for (int x = 0; x < getWidth(); x++) { 
			for (int y = 0; y < getHeight(); y++) {
				SOMPoint cell = new SOMPoint(x, y);
				double distance = cell.distanceTo(centralSOM);
				if(distance < radius){
					rate = rate*(radius-distance)/radius;
					for (int x1 = 0; x1 < getDrawingWidth(); x1++) {
						for (int y1 = 0; y1 < getDrawingHeight(); y1++) {
								double val = Math.random()*rate;
								if(val < rate){
									if(example.isSet(x1, y1)){
										drawings[x][y].set(x1, y1, true);
									}
									
									else{
										drawings[x][y].set(x1, y1, false);
									}
																
								}
							
						}
					}
				}
			}
		}
		
	}
	
	public Color getFillFor(int x, int y, SOMPoint node) {
		/* TODO: Return the correct color for pixel (x, y) at node */
		if(drawings[node.x()][node.y()].isSet(x,y)){
			return Color.BLACK;
		}
		else{
			return Color.WHITE;
		}
	}
	
	public void visualize(Canvas surface) {
		System.out.println("visualizing");
		final double cellWidth = surface.getWidth() / getWidth();
		final double cellHeight = surface.getHeight() / getHeight();
		final double pixWidth = cellWidth / getDrawingWidth();
		final double pixHeight = cellHeight / getDrawingHeight();
		GraphicsContext g = surface.getGraphicsContext2D();
		for (int x = 0; x < getWidth(); x++) { 
			for (int y = 0; y < getHeight(); y++) {
				SOMPoint cell = new SOMPoint(x, y);
				for (int x1 = 0; x1 < getDrawingWidth(); x1++) {
					for (int y1 = 0; y1 < getDrawingHeight(); y1++) {
						g.setFill(getFillFor(x1, y1, cell));
						g.fillRect(cellWidth * x + pixWidth * x1, cellHeight * y + pixHeight * y1, pixWidth, pixHeight);			
					}
				}
			}
		}
	}
}
