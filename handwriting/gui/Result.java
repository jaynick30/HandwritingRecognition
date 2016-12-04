package handwriting.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Result {
	private StringProperty label;
	private DoubleProperty percent;
	private IntegerProperty success, failure;
	
	public Result(String label, int success, int failure) {
		this.label = new SimpleStringProperty(label);
		this.success = new SimpleIntegerProperty(success);
		this.failure = new SimpleIntegerProperty(failure);
		this.percent = new SimpleDoubleProperty(success / (double)(success + failure));
	}
	
	public StringProperty labelProperty() {return label;}
	public DoubleProperty percentProperty() {return percent;}
	public IntegerProperty successProperty() {return success;}
	public IntegerProperty failureProperty() {return failure;}
}
