package life;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import wireworld.BoardInput;

public class CellArray extends JPanel {

    /************ENUM STATES************/ 
    public static final int BACKGROUND = 0;
    public static final int WIRE = 3;
    public static final int HEAD = 1;
    public static final int TAIL = 2;
    /************FIELDS****************/
    private JFrame frame;
    private static int gridToDraw[][];
    private Timer andTimeGoesOn;
    private static LinkedList<Cell> foregroundList;

    /************CONSTRUCTOR***********/
    private CellArray(Dimension d) {
        this.setPreferredSize(new Dimension(d.width - 25, d.height - 5));
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        frame.pack();
        andTimeGoesOn = new Timer(700, update);
        andTimeGoesOn.start();
    }
    /***********NEXT GENERATION VIA ARRAY************/
    private int[][] array_getNextGen() {
        int[][] newGen = new int[32][32];
        for (int i = 1; i < newGen.length - 1; i++) {
            for (int j = 1; j < newGen[i].length - 1; j++) {
                switch (gridToDraw[j][i]) {
                    case BACKGROUND: 
                        newGen[j][i] = BACKGROUND;
                        break;
                    case HEAD: 
                        newGen[j][i] = TAIL;
                        break;
                    case TAIL: 
                        newGen[j][i] = WIRE;
                        break;
                    case WIRE: 
                        int neighbors = checkNeighbors(j,i);
                        if (neighbors>0 && neighbors<3) { 
                            newGen[j][i] = HEAD;
                            break;
                        } else { 
                            newGen[j][i] = WIRE;
                            break;
                        }
                }
            }
        }
        return newGen;
    }
    /************NEXT GENERATION VIA LIST*************/
    private int [][] list_getNextGen(){ 
        Iterator<Cell> it;
        for(it = foregroundList.listIterator(); it.hasNext();){
            Cell cur = it.next();
            switch(cur.getVal()){
                    case HEAD: 
                        cur.setVal(TAIL);
                        break;
                    case TAIL: 
                        cur.setVal(WIRE);
                        break;
                    case WIRE: 
                        int neighbors = checkNeighbors(cur.getCol(),cur.getRow());
                        if (neighbors>0 && neighbors<3) { 
                            cur.setVal(HEAD);
                            break;
                        } else { 
                            break;
                        }
            }
        }
        return listToArr();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GRAY.darker());
        g2d.fill(new Rectangle(0, 0, this.getWidth(), this.getHeight()));
        for (int i = 0; i < gridToDraw.length; i++) {
            for (int j = 0; j < gridToDraw[0].length; j++) {
                switch (gridToDraw[j][i]) {
                    case BACKGROUND: g2d.setColor(Color.BLACK);
                        break;
                    case WIRE: g2d.setColor(Color.WHITE);
                        break;
                    case HEAD: g2d.setColor(Color.RED);
                        break;
                    case TAIL: g2d.setColor(Color.BLUE.brighter());
                        break;
                }
                g2d.fillRect(j * 11, i * 11, 10, 10);
            }
        }
    }

    private Action update = new AbstractAction() { //event to get next generation every 700ms

        @Override
        public void actionPerformed(ActionEvent ae) {
            //gridToDraw = array_getNextGen();
            gridToDraw = list_getNextGen(); //currently drawing with list
            repaint();
        }
    };

    private int checkNeighbors(int j, int i) {
        int counter = 0;
        if (gridToDraw[j - 1][i - 1] == CellArray.HEAD) counter++;
        if (gridToDraw[j - 1][i] == CellArray.HEAD) counter++;
        if (gridToDraw[j - 1][i + 1] == CellArray.HEAD) counter++;
        if (gridToDraw[j][i - 1] == CellArray.HEAD) counter++;
        if (gridToDraw[j][i + 1] == CellArray.HEAD) counter++;
        if (gridToDraw[j + 1][i - 1] == CellArray.HEAD) counter++;
        if (gridToDraw[j + 1][i] == CellArray.HEAD) counter++;
        if (gridToDraw[j + 1][i + 1] == CellArray.HEAD) counter++;
        return counter;
    }
    /****************HELPER METHOD TO EXPORT LIST TO ARRAY*********/
    /****Takes advantage of the way int arrays are initialized. should not be called anywhere else********/
    private int[][] listToArr() { 
        int [][] newGen = new int[32][32];
        Cell cur;
        for(Iterator<Cell> it = foregroundList.listIterator(); it.hasNext();){
            cur = it.next();
            newGen[cur.getCol()][cur.getRow()]=cur.getVal();
        }
        return newGen;
    }
    
    public static void main(String[] args) {
        gridToDraw = new int[32][32];
        BoardInput bIn = new BoardInput(gridToDraw);
        //grid now changed, write non-background cells to list
        foregroundList = new LinkedList<>();
        for(int i = 1; i < gridToDraw.length-1; i++){ 
            for(int j =1; j < gridToDraw[i].length-1; j++){
                if(gridToDraw[j][i]!=BACKGROUND)
                    foregroundList.addLast(new Cell(j,i,gridToDraw[j][i]));
            }
        }
        //if getting next gen by arrays, comment out above loops
        CellArray n = new CellArray(bIn.getPreferredSize());
    }
}
  