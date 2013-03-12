package JumpingButton;

import javax.swing.JButton;

public class Button extends JButton{

    private int scoreMod;
    
    public Button() {
        super();
    }
    public void setScoreMod(int mod){
        scoreMod = mod;
    }
    public int getScoreMod(){
        return scoreMod;
    } 
}