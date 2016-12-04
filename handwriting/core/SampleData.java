package handwriting.core;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import search.core.Duple;

public class SampleData {
	private Map<String,ArrayList<Drawing>> labelToDrawing;
	private int width, height;
	private boolean firstDrawingAdded;
	
	public SampleData() {
		labelToDrawing = new LinkedHashMap<String,ArrayList<Drawing>>();
		firstDrawingAdded = false;
	}
	
	public int numDrawings() {
		int num = 0;
		for (String label: allLabels()) {
			num += numDrawingsFor(label);
		}
		return num;
	}
	
	public int numLabels() {
		return labelToDrawing.size();
	}
	
	public void addLabel(String label) {
		labelToDrawing.put(label, new ArrayList<Drawing>());
	}
	
	public int getDrawingWidth() {return width;}
	public int getDrawingHeight() {return height;}
	
	public void addDrawing(String label, Drawing d) {
		if (firstDrawingAdded) {
			if (d.getWidth() != width || d.getHeight() != height) {
				throw new IllegalArgumentException("size mismatch");
			}
		} else {
			width = d.getWidth();
			height = d.getHeight();
			firstDrawingAdded = true;
		}
		
		if (!hasLabel(label)) {
			addLabel(label);
		}
		labelToDrawing.get(label).add(new Drawing(d));
	}
	
	public boolean hasLabel(String label) {
		return labelToDrawing.containsKey(label);
	}
	
	public int numDrawingsFor(String label) {
		return hasLabel(label) ? labelToDrawing.get(label).size() : 0;
	}
	
	public Drawing getDrawing(String label, int index) {
		return new Drawing(labelToDrawing.get(label).get(index));
	}
	
	public Duple<String,Drawing> getLabelAndDrawing(int index) {
		if (index < 0 || index >= numDrawings()) {
			throw new IndexOutOfBoundsException(index + " > numDrawings(): " + numDrawings());
		}
		for (String label: allLabels()) {
			if (index < numDrawingsFor(label)) {
				return new Duple<>(label, getDrawing(label, index));
			} else {
				index -= numDrawingsFor(label);
			}
		}
		throw new IllegalStateException("This should never happen");
	}
	
	public String getLabelFor(int index) {
		return getLabelAndDrawing(index).getFirst();
	}
	
	public Drawing getDrawing(int index) {
		return getLabelAndDrawing(index).getSecond();
	}
	
	public Set<String> allLabels() {
		return labelToDrawing.keySet();
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (String label: allLabels()) {
			result.append(label + ":");
			for (int i = 0; i < numDrawingsFor(label); ++i) {
				result.append(getDrawing(label, i).toString());
				result.append(":");
			}
			result.deleteCharAt(result.length() - 1);
			result.append("\n");
		}
		return result.toString();
	}
	
	public static SampleData parseDataFrom(Scanner s) {
		SampleData result = new SampleData();
		while (s.hasNextLine()) {
			String line = s.nextLine();
			String[] tokens = line.split(":");
			String label = tokens[0];
			for (int i = 1; i < tokens.length; ++i) {
				result.addDrawing(label, new Drawing(tokens[i]));
			}
		}
		
		return result;
	}
	
	public static SampleData parseDataFrom(File f) throws FileNotFoundException {
		return parseDataFrom(new Scanner(f));
	}
	
	public static SampleData parseDataFrom(String s) throws FileNotFoundException {
		return parseDataFrom(new File(s));
	}
}
