package handwriting.learners;

import java.util.concurrent.ArrayBlockingQueue;

import handwriting.core.*;

public class BaggedWritingRecognition5 implements RecognizerAI {
	Bagger bagger;
	
	
	@Override
	public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {
		this.bagger = new Bagger(WritingRecognition3::new, 5);
		this.bagger.train(data, progress);
	}

	@Override
	public String classify(Drawing d) {
		String winner = this.bagger.classify(d);
		return winner;
		
	}

}
