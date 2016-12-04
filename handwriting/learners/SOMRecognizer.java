package handwriting.learners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import handwriting.core.*;
import handwriting.learners.som.SOMPoint;
import handwriting.learners.som.SelfOrgMap;
import javafx.scene.canvas.Canvas;

public class SOMRecognizer implements RecognizerAI {
	public static double rate = 2.0;
	public static double radius = 3.0;
	private static double numIterations = 100;
	private int width = 25;
	private int height = 25;
	private int drawingWidth = 40;
	private int drawingHeight = 40;
	private String[][] labels = new String[width][height];
	private SampleData data;
	
	public Hashtable<ArrayList<SOMPoint>, String>  map = new Hashtable<ArrayList<SOMPoint>, String>();
	
	ArrayList<SOMPoint> sompointA = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointB = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointC = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointF = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointI = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointK = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointM = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointS = new ArrayList<SOMPoint>();
	ArrayList<SOMPoint> sompointZ = new ArrayList<SOMPoint>();
	
	
	
	

	SelfOrgMap SOM = new SelfOrgMap(this.width, this.height, this.drawingWidth, this.drawingHeight, rate, radius);		
	
	static double[][] encode(Drawing d) {
		double[][] encoding = new double[40][40];
		for (int x = 0; x < d.getWidth(); x++) {
			for (int y = 0; y < d.getHeight(); y++) {
				if (d.isSet(x, y)) {
					encoding[x][y] = 1;
				} else {
					encoding[x][y] = 0;
				}
			}
		}
		return encoding;
	}

	@Override
	public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
		progress.put(0.0);
		for (int j = 0; j < numIterations; j++) {
			progress.put(j / numIterations);
			Set<String> labels = data.allLabels();
			Iterator<String> iter = labels.iterator();
			while (iter.hasNext()) {
				String currentLabel = iter.next();
				int numDrawings = data.numDrawingsFor(currentLabel);
				for (int i = 0; i < numDrawings; i++) {
					SOM.train(data.getDrawing(currentLabel, i));
				}
			}
		}
		for(int j = 0; j < this.width; j++){
			for(int i = 0; i< this.height; i++){
				this.labels[j][i] = "unknown";
			}
		}
		this.data = data;
		
		findBest(data);
		//printSOM();
		
		

	}
	
	private void printSOM(){
		for(int j = 0; j < this.width; j++){
			for(int i = 0; i< this.height; i++){
				System.out.println("label " + labels[j][i]);
			}
		}
	}
	
	public void findBest(SampleData data) {
		int sum = 0;
		int bestDistance = 1600;
		
		for (int x = 0; x < this.width; x++) { 
			for (int y = 0; y < this.height; y++) {
				bestDistance = 1600;
				for(int i = 0; i < data.numDrawings(); i++){
					sum = SOM.getDistance(SOM.drawings[x][y], data.getDrawing(i));
				if(sum < bestDistance){
					bestDistance = sum;
					this.labels[x][y] = data.getLabelFor(i);
					//System.out.println("putting " + data.getLabelFor(i) + " for " + "(" + x + "," + y + ")");
				}
				
				}
			}
		}
	}
	
	public void visualize(Canvas surface) {
		SOM.visualize(surface);
	}

	@Override
	public String classify(Drawing d) {
		/*
		Set<String> labels = data.allLabels();
			Iterator<String> iter = labels.iterator();
			while (iter.hasNext()) {
				String currentLabel = iter.next();
				int numDrawings = data.numDrawingsFor(currentLabel);
				for (int i = 0; i < numDrawings; i++) {
					SOMPoint somp = SOM.bestFor(data.getDrawing(currentLabel, i));
					this.labels[somp.x()][somp.y()] = currentLabel;
				}		
			}
		*/
		
		
		SOMPoint somp = SOM.bestFor(d);
		return this.labels[somp.x()][somp.y()];
	}
}
