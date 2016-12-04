package handwriting.learners;

import static org.junit.Assert.*;

import org.junit.Test;

public class PerceptronTest {
	private double[][] inputs = { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 0, 0 } };
	private double[][] andTargets = { { 1 }, { 0 }, { 0 }, { 0 } };
	private double[][] orTargets = { { 1 }, { 1 }, { 1 }, { 0 } };
	private double[][] xorTargets = { { 0 }, { 1 }, { 1 }, { 0 } };

	@Test
	public void testAnd() {
		trainAndTest(andTargets, 10000, 0.1);
	}

	@Test
	public void testOr() {
		trainAndTest(orTargets, 10000, 0.1);
	}

	@Test
	public void testXor() {
		multiTrainTest(xorTargets, 10000, 0.1);
	}

	public void testResult(PerceptronNet p, double[][] targets) {
		System.out.println("New test");
		for (int i = 0; i < inputs.length; ++i) {
			double[] result = p.compute(inputs[i]);
			System.out.print("input: ");
			for (int k = 0; k < inputs[i].length; ++k) {
				System.out.print(inputs[i][k] + " ");
			}
			System.out.println();
			for (int j = 0; j < result.length; ++j) {
				System.out.println("target: " + targets[i][j] + " output: " + result[j]);
				assertEquals(targets[i][j], result[j], 0.1);
			}
		}
		System.out.println();
	}

	public void trainAndTest(double[][] targets, int iterations, double rate) {
		Perceptron p = new Perceptron(2, 1);
		p.trainN(inputs, targets, iterations, rate);
		testResult(p, targets);
	}

	public void multiTrainTest(double[][] targets, int iterations, double rate) {
		MultiLayer ml = new MultiLayer(2, 3, 1);
		ml.trainN(inputs, targets, iterations, rate);
		testResult(ml, targets);
	}
}
