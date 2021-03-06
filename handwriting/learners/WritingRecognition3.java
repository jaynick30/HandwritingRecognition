package handwriting.learners;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import handwriting.core.*;

public class WritingRecognition3 implements RecognizerAI {
	public static double rate = 0.12;
	private static int hiddenNodes = 60;
	private static int numOutputs = 8;
	private static double numIterations = 1000;

	static MultiLayer multi = new MultiLayer(1600, hiddenNodes, numOutputs);
	static double[] LabelIsI = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	static double[] LabelIsF = { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	static double[] LabelIsB = { 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	static double[] LabelIsA = { 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	static double[] LabelIsC = { 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
	static double[] LabelIsK = { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0 };
	static double[] LabelIsM = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0 };
	static double[] LabelIsS = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

	public static double[] encode(Drawing d) {
		double[] encoding = new double[1600];
		int index = 0;
		for (int i = 0; i < d.getWidth(); i++) {
			for (int j = 0; j < d.getHeight(); j++) {
				if (d.isSet(i, j)) {
					encoding[index] = 1;
					index++;
				} else {
					encoding[index] = 0;
					index++;
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
					double[] inputs = encode(data.getDrawing(currentLabel, i));
					if (currentLabel.equals("a")) {
						multi.train(inputs, LabelIsA, rate);
					}
					if (currentLabel.equals("b")) {
						multi.train(inputs, LabelIsB, rate);
					}
					if (currentLabel.equals("c")) {
						multi.train(inputs, LabelIsC, rate);
					}
					if (currentLabel.equals("f")) {
						multi.train(inputs, LabelIsF, rate);
					}
					if (currentLabel.equals("i")) {
						multi.train(inputs, LabelIsI, rate);
					}
					if (currentLabel.equals("k")) {
						multi.train(inputs, LabelIsK, rate);
					}
					if (currentLabel.equals("m")) {
						multi.train(inputs, LabelIsM, rate);
					}
					if (currentLabel.equals("s")) {
						multi.train(inputs, LabelIsS, rate);
					}
				}
			}
			multi.updateWeights();
		}

	}

	@Override
	public String classify(Drawing d) {
		System.out.println("classifying writer");
		double[] coded = encode(d);
		double[] rawResults = multi.compute(coded);
		double total = 0;
		for (int i = 0; i < rawResults.length; i++) {
			total += rawResults[i];
			//System.out.println("result " + i + " is " + rawResults[i]);
		}
		System.out.println("total is " + total);
		if (total < 1) {
			return "i";
		}
		if (total > 1 && total < 2) {
			return "f";
		}
		if (total > 2 && total < 3) {
			return "b";
		}
		if (total > 3 && total < 4) {
			return "a";
		}
		if (total > 4 && total < 5) {
			return "c";
		}
		if (total > 5 && total < 6) {
			return "m";
		}
		if (total > 6 && total < 7) {
			return "k";
		}
		if (total > 7) {
			return "s";
		} else {
			return "Unknown";
		}
	}

}
