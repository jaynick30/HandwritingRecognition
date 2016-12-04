package handwriting.learners;

abstract public class PerceptronNet {
	abstract public double[] compute(double[] inputs);

	abstract public void train(double[] inputs, double[] targets, double rate);

	abstract public void updateWeights();

	abstract public int numInputNodes();

	abstract public int numOutputNodes();

	public void trainN(double[][] inputs, double[][] targets, int iterations, double rate) {
		for (int i = 0; i < iterations; ++i) {
			for (int j = 0; j < inputs.length; ++j) {
				train(inputs[j], targets[j], rate);
			}
			updateWeights();
		}
	}

	public static double gradient(double fOfX) {
		return fOfX * (1.0 - fOfX);
	}

	public static double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	public static void checkArgs(int target, int passed, String msg) {
		if (target != passed) {
			throw new IllegalArgumentException(String.format("Must have %d %s nodes, not %d", target, msg, passed));
		}
	}

	public static void checkBound(double value, double min, double max, String name) {
		if (value < min || value > max) {
			throw new IllegalArgumentException(
					String.format("%s %4.2f outside of [%4.2f,%4.2f]", name, value, min, max));
		}
	}

	public void checkTrain(double[] inputs, double[] targets, double rate) {
		checkArgs(numInputNodes(), inputs.length, "input");
		checkArgs(numOutputNodes(), targets.length, "output");
		checkBound(rate, 0, 1, "Learning rate");
	}

	public void checkCompute(double[] inputs) {
		checkArgs(numInputNodes(), inputs.length, "input");
	}
}
