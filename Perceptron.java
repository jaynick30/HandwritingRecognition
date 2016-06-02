package handwriting.learners;

public class Perceptron extends PerceptronNet {
    // First index is the input; second index is the output
    private double[][] weights;
    private double[][] deltas;
    private double[] outputs;
    private double[] errors;
    
    private int numInputs, numOutputs;
    
    protected double output(int i) {return outputs[i];}
    protected double error(int i) {return errors[i];}
    protected double weight(int i, int j) {return weights[i][j];}
    
    // Post: error(i) == error
    protected void setError(int i, double error) {errors[i] = error;}
    
    public int numInputNodes() {return numInputs;}
    public int numOutputNodes() {return numOutputs;}
    public int threshold() {return numInputNodes();}
    
    // returns number of inputs including the threshold
    public int numInputPaths() {return numInputNodes() + 1;}
    
    public Perceptron(int numIn, int numOut) {
        numInputs = numIn;
        numOutputs = numOut;
        
        initArrays();
        
        for (int j = 0; j < numInputPaths(); ++j) {
            for (int i = 0; i < numOutputNodes(); ++i) {
            	weights[j][i] = (Math.random() * 2.0) - 1.0;
                deltas[j][i] = 0.0;
            }
        }
        
        for (int i = 0; i < numOutputNodes(); ++i) {
            outputs[i] = 0.0;
            errors[i] = 0.0;
            weights[threshold()][i] = 0.0;
        }
    }
    
    // Creates a new Perceptron with the same weights as other
    public Perceptron(Perceptron other) {
        numInputs = other.numInputNodes();
        numOutputs = other.numOutputNodes();
        
        initArrays();
        
        for (int j = 0; j < numInputPaths(); ++j) {
            for (int i = 0; i < numOutputNodes(); ++i) {
                weights[j][i] = other.weights[j][i];
                deltas[j][i] = 0.0;
            }
        }
        
        for (int i = 0; i < numOutputNodes(); ++i) {
            outputs[i] = 0.0;
            errors[i] = 0.0;
            weights[threshold()][i] = 0.0;
        }
    }
    
    private void initArrays() {
        weights = new double[numInputPaths()][numOutputNodes()];
        deltas = new double[numInputPaths()][numOutputNodes()];
        outputs = new double[numOutputNodes()];
        errors = new double[numOutputNodes()];
    }
    
    public double getWeightFromTo(int inputNode, int outputNode) {
        return weights[inputNode][outputNode];
    }
    
    public void setWeightFromTo(double w, int inputNode, int outputNode) {
        weights[inputNode][outputNode] = w;
    }
    
    public void setThresholdWeight(double w, int outputNode) {
        weights[threshold()][outputNode] = w;
    }
    
    public double[] compute(double[] inputs) {
    	checkCompute(inputs);
    	for(int i = 0; i < numOutputs; i++){
    		double sum = 0;
    		for(int j = 0; j < numInputs; j++){
    			sum += inputs[j]*getWeightFromTo(j,i);
    		}
    		sum += -1*weights[threshold()][i]; 
    		
    		double thresh = sigmoid(sum);
    		outputs[i] = thresh;
    		
    	}
        return outputs;
    }
    
    // This method should be called by train() or backpropagate()
    // It accumulates the deltas to reflect the recently computed errors
    public void addToWeightDeltas(double[] inputs, double rate) {
        /* TODO:
           For each output:
             For each input:
               Calculate how this output wants the weight changed
               Add that result to the delta[][] for that weight
         */
    	for(int j = 0; j < numOutputs; j++){
    		double grad = gradient(outputs[j]);
    		for(int i = 0; i < numInputs; i++){    			
    			double error = inputs[i]*errors[j]*rate*grad;
    			
    			deltas[i][j] += error;
    		}
    		deltas[threshold()][j] += -1*errors[j]*rate*grad;
    	}
    }
    
    // Before calling this method, train() has been called for all input/output pairs
    // Afterward, weights are updated for one training cycle, and
    // the incremental deltas are reset to zero for next cycle.
    public void updateWeights() {
        for (int j = 0; j < numInputPaths(); ++j) {
            for (int i = 0; i < numOutputNodes(); ++i) {
                weights[j][i] += deltas[j][i];
                deltas[j][i] = 0.0;
            }
        }
    }
    
    public void train(double[] inputs, double[] targets, double rate) {
    	checkTrain(inputs, targets, rate);
        compute(inputs);
        for (int i = 0; i < numOutputNodes(); ++i) {
            setError(i, targets[i] - output(i));
        }
        addToWeightDeltas(inputs, rate);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numOutputNodes(); ++i) {
            sb.append("Perceptron output node ");
            sb.append(i);
            sb.append(" threshold: ");
            sb.append(weights[threshold()][i]);
            sb.append("\nIncoming weights:\n");
            for (int j = 0; j < numInputNodes(); ++j) {
                sb.append(weights[j][i]);
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
