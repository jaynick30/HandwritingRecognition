package handwriting.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;

import handwriting.core.Drawing;
import handwriting.core.RecognizerAI;
import handwriting.core.SampleData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import search.core.AIReflector;
import search.core.Duple;
import search.core.Histogram;

public class DrawingEditorController {
	@FXML
	Button clear;
	
	@FXML
	Button record;
	
	@FXML
	Button drawErase;
	
	@FXML
	Button classify;
	
	@FXML
	Button train;
	
	@FXML
	Canvas canvas;
	
	@FXML
	Canvas visualization;
	
	@FXML
	ChoiceBox<String> labelChoice;
	
	@FXML
	ChoiceBox<Integer> drawingChoice;
	
	@FXML
	ChoiceBox<String> algorithmChoice;
	
	@FXML
	TextField recordingClassificationLabel;
	
	@FXML
	ProgressBar trainingProgress;
	
	@FXML
	MenuItem newData;
	
	@FXML
	MenuItem openData;
	
	@FXML
	MenuItem saveData;
	
	@FXML
	Button testAll;
	
	@FXML
	TextField testResults;
	
	@FXML
	TableView<Result> resultTable;
	
	@FXML
	TableColumn<TableView<Result>,String> labels;
	
	@FXML
	TableColumn<TableView<Result>,Double> percents;
	
	@FXML
	TableColumn<TableView<Result>,Integer> rights;
	
	@FXML
	TableColumn<TableView<Result>,Integer> wrongs;
	
	RecognizerAI trainer;
	
	SampleData drawings;
	
	boolean isDrawing;
	
	Drawing sketch;
	
	AIReflector<RecognizerAI> ais;
	
	public final static int DRAWING_WIDTH = 40, DRAWING_HEIGHT = 40;
	
	@FXML
	void initialize() {
		setupVars();
		setupMenus();
		setupButtons();
		setupCanvas();
		setupChoiceBoxes();
		setupTable();
		trainingProgress.setProgress(1.0);
	}
	
	void setupTable() {
		labels.setCellValueFactory(new PropertyValueFactory<TableView<Result>,String>("label"));
		percents.setCellValueFactory(new PropertyValueFactory<TableView<Result>,Double>("percent"));
		rights.setCellValueFactory(new PropertyValueFactory<TableView<Result>,Integer>("success"));
		wrongs.setCellValueFactory(new PropertyValueFactory<TableView<Result>,Integer>("failure"));
	}
	
	void setupVars() {
		sketch = new Drawing(DRAWING_WIDTH, DRAWING_HEIGHT);
		newData();
		setupDrawErase();
		setupDefaultTrainer();
		findTrainers();	
	}
	
	void setupDefaultTrainer() {
		trainer = new RecognizerAI() {
			@Override
			public void train(SampleData data, ArrayBlockingQueue<Double> progress) throws InterruptedException {}
			@Override
			public String classify(Drawing d) {return "Unknown";}};
	}
	
	void setupDrawErase() {
		isDrawing = true;
		setDrawEraseText();
		drawErase.setOnAction(event -> {
			isDrawing = !isDrawing;
			setDrawEraseText();
		});		
	}
	
	void setDrawEraseText() {
		drawErase.setText(isDrawing ? "Erase" : "Draw");
	}
	
	void newData() {
		drawings = new SampleData();
		labelChoice.getItems().clear();
		drawingChoice.getItems().clear();
	}
	
	void setupMenus() {
		newData.setOnAction(event -> newData());
		openData.setOnAction(event -> openDataFile());
		saveData.setOnAction(event -> saveDataFile());
	}
	
	void saveDataFile() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select test data");
		File savee = chooser.showSaveDialog(null);
		if (savee != null) {
			try {
				PrintWriter out = new PrintWriter(savee);
				out.println(drawings.toString());
				out.close();
			} catch (FileNotFoundException e) {
				oops(e);
			}
		}
	}
	
	void findTrainers() {
		ais = new AIReflector<>(RecognizerAI.class, "handwriting.learners");
		for (String typeName: ais.getTypeNames()) {
			algorithmChoice.getItems().add(typeName);
		}
		if (algorithmChoice.getItems().size() > 0) {
			algorithmChoice.getSelectionModel().select(0);
		}
	}
	
	void setupButtons() {
		setupRecord();
		setupTrain();
		setupTest();

		classify.setOnAction(event -> 
			recordingClassificationLabel.setText(trainer.classify(sketch)));
		
		clear.setOnAction(event -> clearCanvas());
	}
	
	void setupChoiceBoxes() {
		labelChoice.getSelectionModel().selectedItemProperty().addListener((v, vOld, vNew) -> resetDrawingList(vNew));
		
		drawingChoice.getSelectionModel().selectedIndexProperty().addListener((v,vOld,vNew) -> {
			int choice = vNew.intValue();
			if (choice >= 0) {
				clearCanvas();
				sketch = drawings.getDrawing(getCurrentLabel(), vNew.intValue());
				for (int x = 0; x < sketch.getWidth(); x++) {
					for (int y = 0; y < sketch.getHeight(); y++) {
						if (sketch.isSet(x, y)) plot(x, y);
					}
				}
			}
		});		
	}
	
	void setupRecord() {
		record.setOnAction(event -> {
			String label = recordingClassificationLabel.getText();
			if (label.length() > 0) {
				addSample(label, sketch);
				clearCanvas();
			} else {
				info("No label specified");
			}
		});		
	}
	
	void setupTrain() {
		ArrayBlockingQueue<Double> progress = new ArrayBlockingQueue<>(2);
		startProgressThread(progress);
		
		train.setOnAction(event -> {
			testResults.setText("");
			ArrayBlockingQueue<RecognizerAI> result = new ArrayBlockingQueue<>(1);
			startTrainingDoneThread(result);
			startTrainingThread(progress, result);
		});		
	}
	
	void startProgressThread(ArrayBlockingQueue<Double> progress) {
		new Thread(() -> {
			double prog = 0;
			for (;;) {
				trainingProgress.setProgress(prog);
				try {
					prog = progress.take();
				} catch (Exception e) {
					Platform.runLater(() -> oops(e));
				}
			}
		}).start();		
	}
	
	void startTrainingDoneThread(ArrayBlockingQueue<RecognizerAI> result) {
		new Thread(() -> {
			try {trainer = result.take();} 
			catch (Exception e) {}
			Platform.runLater(() -> trainer.visualize(visualization));
			Platform.runLater(() -> info("Training finished"));
		}).start();		
	}
	
	void startTrainingThread(ArrayBlockingQueue<Double> progress, ArrayBlockingQueue<RecognizerAI> result) {
		new Thread(() -> {
			try {
				progress.put(0.0);
				RecognizerAI created = ais.newInstanceOf(algorithmChoice.getSelectionModel().getSelectedItem());
				created.train(drawings, progress);
				result.put(created);
			} catch (Exception e) {
				Platform.runLater(() -> oops(e));
			}				
		}).start();		
	}
	
	void setupTest() {
		testAll.setOnAction(event -> {
			testResults.setText("");
			File testFile = getDataFile();
			if (testFile != null) {
				try {
					runTests(SampleData.parseDataFrom(testFile));
				} catch (FileNotFoundException e) {
					oops(e);
				}
			}
		});
	}
	
	void runTests(SampleData testData) {
		int numCorrect = 0;
		Histogram<String> correct = new Histogram<>(), incorrect = new Histogram<>();
		for (int i = 0; i < testData.numDrawings(); i++) {
			Duple<String,Drawing> test = testData.getLabelAndDrawing(i);
			if (trainer.classify(test.getSecond()).equals(test.getFirst())) {
				numCorrect += 1;
				correct.bump(test.getFirst());
			} else {
				incorrect.bump(test.getFirst());
			}
		}
		double percent = 100.0 * numCorrect / testData.numDrawings();
		testResults.setText(String.format("%d/%d (%4.2f%%) correct", numCorrect, testData.numDrawings(), percent));		
		resetTable(testData, correct, incorrect);
	}
	
	void resetTable(SampleData testData, Histogram<String> correct, Histogram<String> incorrect) {
		resultTable.getItems().clear();
		for (String label: testData.allLabels()) {
			resultTable.getItems().add(new Result(label, correct.getCountFor(label), incorrect.getCountFor(label)));
		}
	}
	
	void oops(Exception exc) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(String.format("Exception: %s\nMessage: %s\n", exc.getClass().toString(), exc.getMessage()));
		alert.show();
		exc.printStackTrace();
	}
	
	void info(String s) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(s);
		alert.show();
	}
	
	void openDataFile() {
		File dataFile = getDataFile();
		if (dataFile != null) {
			try {
				drawings = SampleData.parseDataFrom(dataFile);
				resetLabels();
				clearCanvas();
			} catch (FileNotFoundException e) {
				oops(e);
			}
		}
	}
	
	File getDataFile() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select test data");
		return chooser.showOpenDialog(null);
	}
	
	void addSample(String label, Drawing sample) {
		drawings.addDrawing(label, sample);
		if (!labelChoice.getItems().contains(label)) {
			labelChoice.getItems().add(label);
		}
		labelChoice.getSelectionModel().select(label);
		resetDrawingList(label);
	}
	
	void resetLabels() {
		labelChoice.getItems().clear();
		for (String label: drawings.allLabels()) {
			labelChoice.getItems().add(label);
		}
		if (labelChoice.getItems().size() > 0) {
			labelChoice.getSelectionModel().select(0);
			resetDrawingList(drawings.allLabels().iterator().next());
		}
	}
	
	void resetDrawingList(String label) {
		drawingChoice.getItems().clear();
		for (int i = 0; i < drawings.numDrawingsFor(label); i++) {
			drawingChoice.getItems().add(i);
		}
	}
	
	String getCurrentLabel() {
		return labelChoice.getSelectionModel().getSelectedItem();
	}
	
	int getCurrentDrawing() {
		return drawingChoice.getSelectionModel().getSelectedItem();
	}
	
	void clearCanvas() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		sketch.clear();
	}
	
	void setupCanvas() {
		clearCanvas();
		
		canvas.setOnMouseDragged(mouse -> {
			int xGrid = (int)(mouse.getX() / xCell());
			int yGrid = (int)(mouse.getY() / yCell());
			plot(xGrid, yGrid);
			plot(xGrid - 1, yGrid);
			plot(xGrid + 1, yGrid);
			plot(xGrid - 1, yGrid - 1);
			plot(xGrid, yGrid - 1);
			plot(xGrid + 1, yGrid - 1);
			plot(xGrid - 1, yGrid + 1);
			plot(xGrid, yGrid + 1);
			plot(xGrid + 1, yGrid + 1);
		});
	}
	
	double xCell() {return canvas.getWidth() / sketch.getWidth();}
	double yCell() {return canvas.getHeight() / sketch.getHeight();}
	
	void plot(int x, int y) {
		if (x >= 0 && x < sketch.getWidth() && y >= 0 && y < sketch.getHeight()) {
			sketch.set(x, y, isDrawing);		
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFill(isDrawing ? Color.BLACK : Color.WHITE);
			gc.fillRect(x * xCell(), y * yCell(), xCell(), yCell());
		}
	}
}
