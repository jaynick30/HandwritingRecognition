package handwriting.learners;

import java.util.concurrent.ArrayBlockingQueue;

import handwriting.core.*;

public class OverFitter implements RecognizerAI {
	private SampleData data;

	public OverFitter() {
		data = new SampleData();
	}

	@Override
	public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
		this.data = data;
		progress.put(1.0);
	}

	@Override
	public String classify(Drawing d) {
		for (String label : data.allLabels()) {
			for (int i = 0; i < data.numDrawingsFor(label); ++i) {
				if (d.equals(data.getDrawing(label, i))) {
					return label;
				}
			}
		}

		return "Unknown";
	}
}
