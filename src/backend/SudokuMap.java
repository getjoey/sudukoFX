package backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class SudokuMap {

    static SudokuMap instance = null;
    private SmallSquare[][] gridMapSave;
    private SmallSquare[][] gridMap;
    private final int gridSize;
    //private int minAmount = 1000;
    private int sanityCheck = 6000;
    private Random ran;
    private ArrayList[][] possibilities;

    private SudokuMap(int gridSize, int toRemove) {
        this.ran = new Random();
        this.gridSize = gridSize;

        //init map & possiblities
        gridMap = new SmallSquare[gridSize][gridSize];
        for(int x=0; x<gridSize; x++) {
            for(int y=0; y<gridSize; y++) {
                gridMap[x][y] = new SmallSquare(0, false); //map init
            }
        }
        gridMapSave = gridMap.clone();
        possibilities = new ArrayList[gridSize][gridSize];
        updatePossibilities();

        constructGridMap();
        setGame(toRemove);
    }

    public static SudokuMap getInstance() {
       return getInstance(0);
    }

    public static SudokuMap getInstance(int toRemove) {
        if(instance == null) {
            instance = new SudokuMap(9, toRemove);
        }
        return instance;
    }

    /*
    * Creates a random soduku puzzle based on dimensions
     */
    private void constructGridMap() {
        //add given solid numbers at beginning
        int sanity = 0;
        while (!isComplete()) {

            if(sanity > sanityCheck) {
                for(int x=0; x<gridSize; x++) {
                    for(int y=0; y<gridSize; y++) {
                        gridMap[x][y] = new SmallSquare(0, false); //map init
                    }
                }
                updatePossibilities();
                sanity = 0;
            } else {
                sanity++;
            }

            //Display.display(this);

            int x = ran.nextInt(gridSize);
            int y = ran.nextInt(gridSize);

            //check its an empty spot
            if (possibilities[x][y].size() !=0) {
                int r = ran.nextInt(possibilities[x][y].size());
                int aa = (Integer) possibilities[x][y].get(r);
                gridMapSave = gridMap.clone();
                gridMap[x][y] = new SmallSquare(aa, true);
                //Display.display(this);
                updatePossibilities();
                if (!isPossibleToSolve()) {
                    gridMap[x][y] = new SmallSquare(0, false);
                    updatePossibilities();
                }
            }

        }
    }

    /*
    * Gets all possiblities smallSquare can be (checks quadrant and lines, for nums it can be
    * WORKS DONT TOUCH
     */
    private ArrayList<Integer> getRemainingValidChoicesPerSmallSquare(int x, int y) {
        int quadrantx = x / (gridSize/3);
        int quadranty = y / (gridSize/3);
        quadrantx = (int) Math.floor(quadrantx);
        quadranty = (int) Math.floor(quadranty);

        ArrayList<Integer> listOfNumsQuadrant = new ArrayList<Integer>();
        for(int i=0; i<(gridSize/3); i++) {
            for (int j = 0; j < (gridSize / 3); j++) {
                int actualx = i+quadrantx*(gridSize/3);
                int actualy = j+quadranty*(gridSize/3);
                listOfNumsQuadrant.add(gridMap[actualx][actualy].getValue());
            }
        }

        HashSet<Integer> listOfNumsLineHori = new HashSet<Integer>();
        for(int i=0; i<gridSize; i++) {
            if(gridMap[i][y].getValue()!=0) {
                listOfNumsLineHori.add(gridMap[i][y].getValue());
            }
        }

        HashSet<Integer> listOfNumsLineVert = new  HashSet<Integer>();
        for(int i=0; i<gridSize; i++) {
            if(gridMap[x][i].getValue()!=0) {
                listOfNumsLineVert.add(gridMap[x][i].getValue());
            }
        }

        ArrayList<Integer> listOfNumsMissing = new ArrayList<Integer>();
        for(int i=1; i<gridSize+1; i++) {
            if(!listOfNumsQuadrant.contains(i) && !listOfNumsLineHori.contains(i) && !listOfNumsLineVert.contains(i)) {
               listOfNumsMissing.add(i);
            }
        }

        return listOfNumsMissing;
    }

    /*
    * updates possibility for each square
    * Not opitimized (should only update for those affected)
    * WORKS DONT TOUCH
     */
    private void updatePossibilities() {
        for(int x=0; x<gridSize; x++) {
            for(int y=0; y<gridSize; y++) {
                //if(gridMap[x][y].getValue() == 0) {
                    possibilities[x][y] = getRemainingValidChoicesPerSmallSquare(x,y);
                //}
                //else {
                //    possibilities[x][y].clear();
                //}
            }
        }
    }

    private boolean reinforce() {

        for(int x=0; x<gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if(possibilities[x][y].size() == 1 ) {
                    int val = (Integer) possibilities[x][y].get(0);
                    if(isMoveValid(val,x,y)) {
                        gridMap[x][y].setValue((Integer)possibilities[x][y].get(0));
                    }
                    else {
                        gridMap = gridMapSave.clone();
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private boolean isLinesValid(int x, int y) {
        //hori
        ArrayList<Integer> missingFromLineX = new ArrayList<Integer>();
        for(int i=1; i<gridSize+1; i++) {
            missingFromLineX.add(i);
        }
        HashSet<Integer> inLineX = new HashSet<Integer>();
        for(int i=0; i<gridSize; i++) {
            inLineX.add(gridMap[i][y].getValue());
        }
        missingFromLineX.removeAll(inLineX);

        HashSet<Integer> possibleX = new HashSet<Integer>();
        for(int i=0; i<gridSize; i++) {
            possibleX.addAll(possibilities[i][y]);
        }
        if(!possibleX.containsAll(missingFromLineX)) {
            return false;
        }

        //vert
        ArrayList<Integer> missingFromLineY = new ArrayList<Integer>();
        for(int i=1; i<gridSize+1; i++) {
            missingFromLineY.add(i);
        }
        HashSet<Integer> inLineY = new HashSet<Integer>();
        for(int i=0; i<gridSize; i++) {
            inLineY.add(gridMap[x][i].getValue());
        }
        missingFromLineY.removeAll(inLineY);

        HashSet<Integer> possibleY = new HashSet<Integer>();
        for(int i=0; i<gridSize; i++) {
            possibleY.addAll(possibilities[x][i]);
        }
        if(!possibleY.containsAll(missingFromLineY)) {
            return false;
        }


        return true;
    }

    private boolean isQuadrantValid(int x, int y) {
        int quadrantx = x / (gridSize/3);
        int quadranty = y / (gridSize/3);
        quadrantx = (int) Math.floor(quadrantx);
        quadranty = (int) Math.floor(quadranty);

        ArrayList<Integer> missingFromQuadrant = new ArrayList<Integer>();
        for(int i=1; i<gridSize+1; i++) {
            missingFromQuadrant.add(i);
        }

        HashSet<Integer> allPossibilitiesForQuadrant = new HashSet<Integer>();
        HashSet<Integer> inQuadrant = new  HashSet<Integer>();
        for(int i=0; i<(gridSize/3); i++) {
            for(int j=0; j<(gridSize/3); j++) {
                int actualx = i+quadrantx*(gridSize/3);
                int actualy = j+quadranty*(gridSize/3);

                allPossibilitiesForQuadrant.addAll(possibilities[actualx][actualy]);
                if(gridMap[actualx][actualy].getValue()!=0) {
                    inQuadrant.add(gridMap[actualx][actualy].getValue());
                }

            }
        }
        missingFromQuadrant.removeAll(inQuadrant);

        if(!allPossibilitiesForQuadrant.containsAll(missingFromQuadrant)) {
            return false;
        }

        return true;
    }

    private boolean isPossibleToSolve() {
        if(!reinforce()) {
            return false;
        }
        for(int x=0; x<gridSize; x++) {
            for(int y=0; y<gridSize; y++) {
                if(!isLinesValid(x,y) && !isQuadrantValid(x,y)) {
                    return false;
                }
            }
        }
        return true;
    }



    //GAME LOGIC
    public boolean isMoveValid(int val, int x, int y) {
        if(checkBigSquare(val,x,y) && checkLines(val,x,y)){
            return true;
        }
        return false;
    }

    private boolean checkLines(int val, int x, int y) {
        for(int i=0; i<gridSize; i++) {
            if(gridMap[x][i].getValue() == val && i != y) {
                return false;
            }
            if(gridMap[i][y].getValue() == val  && i != x) {
                return false;
            }
        }
        return true;
    }
    private boolean checkBigSquare(int val, int x, int y) {
        int quadrantx = x / (gridSize/3);
        int quadranty = y / (gridSize/3);
        quadrantx = (int) Math.floor(quadrantx);
        quadranty = (int) Math.floor(quadranty);

        for(int i=0; i<(gridSize/3); i++) {
            for(int j=0; j<(gridSize/3); j++) {

                int actualx = i+quadrantx*(gridSize/3);
                int actualy = j+quadranty*(gridSize/3);
                // System.out.println("A "+actualx+","+actualy);
                if(gridMap[actualx][actualy].getValue() == val) {
                    if( actualx != x && actualy != y) {
                        return false;
                    }

                }
            }
        }
        return true;
    }
    //check all lines are 1-9
    //check all big squares are 1-9
    //NOT OPTIMIZED TO DO..
    public boolean isComplete(){
        for(int x=0; x<gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                //boolean l = checkLines(0, x, y);
               // boolean b = checkBigSquare(0, x, y);
                //if(!l  || !b){
                //    return false;
                //}
                if(gridMap[x][y].getValue() == 0) {
                    return false;
                }
            }
        }
        //Display.display(this);
        return true;
    }

    public SmallSquare[][] getGridMap() {
        return gridMap;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGame(int toRemove) {
        int removed = 0;
        Random ran = new Random();
        while(removed != toRemove){
            int x = ran.nextInt(9);
            int y = ran.nextInt(9);

            if(gridMap[x][y].getValue() != 0) {
                gridMap[x][y].setValue(0);
                removed++;
            }
        }
    }

}
