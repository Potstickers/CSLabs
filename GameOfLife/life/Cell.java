/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package life;


public class Cell {
    private int col, row, value;
    
    public Cell(int c, int r, int v){
        col = c;
        row = r;
        value = v;
    }
    
    public int getCol(){
        return col;
    }
    public int getRow(){
        return row;
    }
    public int getVal(){
        return value;
    }
    public void setVal(int newv){
        value = newv;
    }
}
