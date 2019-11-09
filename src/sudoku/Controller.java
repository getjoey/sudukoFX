package sudoku;

import backend.SmallSquare;
import backend.SudokuMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    private double gridDimension;
    private double squareDimension;

    private Pair<Integer,Integer> focusedSquare;

    private SmallSquare[][] numberData = new SmallSquare[9][9];

    private SudokuMap map;

    @FXML
    private void drawCanvas(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.setFocusTraversable(true);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gridDimension = canvas.getHeight();
        squareDimension = gridDimension / 9;

        map = new SudokuMap(9);
        map.setGame(10);
        numberData = map.getGridMap();

        drawGrid();
        repaintData();

        canvas.setOnMouseClicked(event -> {
            mouseClick(event);
        });

        canvas.setOnKeyPressed(event -> {
            keyPress(event);
        });

    }

    private void mouseClick(MouseEvent event) {
        Pair<Integer,Integer> location = getSquareLocation(event.getX(), event.getY());
        focusedSquare = location;
        highlightSquare(location);
    }

    private void keyPress(KeyEvent event) {
        if (focusedSquare != null) {
            Integer value = parseKeyCodeToNumber(event.getText());
            if(value != null) {
                if(value == 0) {
                    numberData[focusedSquare.getKey()][focusedSquare.getValue()].setValue(0);
                }
                else if(map.isMoveValid(value, focusedSquare.getKey(),focusedSquare.getValue())) {
                    numberData[focusedSquare.getKey()][focusedSquare.getValue()].setValue(value);
                }
            }
            gc.clearRect(0,0,gridDimension,gridDimension);
            drawGrid();
            highlightSquare(focusedSquare);
            repaintData();
        }
    }

    private Integer parseKeyCodeToNumber(String str) {
        if(str.length() != 1) {
            return null;
        }
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(str);
        boolean valid = m.matches();
        if(valid) {
            Integer value = Integer.parseInt(str);
            return value;
        }
        return null;
    }

    private void repaintData() {
        for(int x=0 ; x<9; x++) {
            for(int y=0; y<9; y++) {
                if(numberData[x][y] != null && numberData[x][y].getValue() != 0) {
                    drawNumber(numberData[x][y].getValue(), x, y);
                }
            }
        }
    }

    private void drawNumber(int num, int xx, int yy) {
        double x = xx * squareDimension + squareDimension/2;
        double y = yy * squareDimension + squareDimension/2;
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(22));
        gc.fillText(""+num,x-7,y+7);
    }

    private void highlightSquare(Pair<Integer,Integer> location) {

        double x1 = focusedSquare.getKey() * squareDimension;
        double x2 = squareDimension;
        double y1 = focusedSquare.getValue() * squareDimension;
        double y2 = squareDimension;

        gc.clearRect(0,0,gridDimension,gridDimension);
        gc.setFill(Color.GRAY);
        gc.fillRect(x1,y1,x2,y2);
        drawGrid();
        repaintData();
    }

    private Pair<Integer,Integer> getSquareLocation(double x, double y) {
        double xloc = Math.floor(x/squareDimension);
        double yloc = Math.floor(y/squareDimension);
        return new Pair<Integer, Integer>((int)xloc,(int)yloc);
    }

    private void drawGrid() {
        //vertical
        for(int i=0; i<=9; i++) {
            gc.setLineWidth(1);
            if(i%3==0) {
              gc.setLineWidth(3);
            }
            gc.strokeLine(i*squareDimension,0,i*squareDimension,gridDimension);
        }
        //horizontal
        for(int i=0; i<=9; i++) {
            gc.setLineWidth(1);
            if(i%3==0) {
                gc.setLineWidth(3);
            }
            gc.strokeLine(0,i*squareDimension, gridDimension, i*squareDimension);
        }
    }

}
