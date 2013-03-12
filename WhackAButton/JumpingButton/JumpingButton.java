package JumpingButton;

import com.sun.media.sound.JavaSoundAudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class JumpingButton extends JFrame implements ActionListener {

    /************FIELDS USED********************/
    private int points = 0;
    private Random rand = new Random();
    private boolean distractionEvent = false;
    private Timer dTimer; //distraction event timeR
    private Timer tTimer; //timer for time display
    private int secondsElapsed = 0; //automated by tTimer,allows for updating in time label
    /************FRAME+COMPONENTS***************/
    private JFrame frame;
    private JPanel panel;
    private JPanel southPanel;
    private JLabel pointsLabel;
    private JLabel timerLabel;
    private Button[] buttons;
    private JLabel distractLabel;

    public static void main(String[] args) throws InterruptedException {
        JumpingButton jump = new JumpingButton(9); //one line main? is this acceptable practice?
    }

    /*************THE CONSTRUCTOR***********************/
    private JumpingButton(int numButtons) {
        //////////////
        //init frame//
        //////////////
        frame = new JFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setTitle("Whack-a-Button!!!! ft. nyan cat =^-^=");
        frame.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setResizable(false);
        //////////////////////////////////////////
        //init panel as subcontainer for buttons//
        //////////////////////////////////////////
        panel = new JPanel();
        panel.setBackground(new Color(112, 192, 223));
        panel.setLayout(null);
        panel.setSize(800, frame.getHeight());
        frame.add(panel, BorderLayout.CENTER);
        //////////////////////////////////////
        //INIT LABELS TO DISPLAY INFORMATION//
        //////////////////////////////////////
        southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 2));
        frame.add(southPanel, BorderLayout.SOUTH);
        pointsLabel = new JLabel(Integer.toString(points));
        pointsLabel.setFont(new Font("Calibri", Font.PLAIN, 24));
        pointsLabel.setBorder(BorderFactory.createTitledBorder("Points: "));
        timerLabel = new JLabel(Integer.toString(secondsElapsed));
        timerLabel.setFont(new Font("Calibri", Font.PLAIN, 24));
        timerLabel.setBorder(BorderFactory.createTitledBorder("Time: "));
        southPanel.add(pointsLabel);
        southPanel.add(timerLabel);
        ////////////////
        //INIT BUTTONS//
        ////////////////
        buttons = new Button[numButtons];
        int basic = rand.nextInt(numButtons); //index for 1 point
        int jackpot = rand.nextInt(numButtons); //index for 3 point
        int stabFoot = rand.nextInt(numButtons); //index for -1 point
        for (int i = 0; i < numButtons; i++) {
            buttons[i] = new Button();
            buttons[i].setIcon(new ImageIcon("click.png"));
            buttons[i].setPressedIcon(new ImageIcon("pressed.png"));
            buttons[i].setScoreMod(0);
            if (i == 0) {
                buttons[i].setBounds((int) frame.getBounds().getCenterX() - (8 * 50), (int) frame.getBounds().getCenterX() - (7 * 50), 50, 50);
            } else {
                buttons[i].setBounds((int) buttons[i - 1].getBounds().getMaxX() + 50, buttons[i - 1].getY(), 50, 50);
            }
            panel.add(buttons[i]);
        }
        ////////////////////////////////
        /////ALLOCATE POINT MODS////////
        ////////////////////////////////
        buttons[basic].setScoreMod(1);
        buttons[basic].addActionListener(this);
        while (buttons[jackpot].getScoreMod() != 0) {
            jackpot = rand.nextInt(numButtons);
        }
        buttons[jackpot].setScoreMod(3);
        buttons[jackpot].addActionListener(this);
        while (buttons[stabFoot].getScoreMod() != 0) {
            stabFoot = rand.nextInt(numButtons);
        }
        buttons[stabFoot].setScoreMod(-1);
        buttons[stabFoot].addActionListener(this);
        /////////////////////////////////
        /////////UNENDING TIMERS/////////
        /////////////////////////////////
        new Timer(100, changeColor).start(); //time background changer
        new Timer(80, buttonsJump).start(); //timer for jumping button animation
    }

    /*****************MAIN LOGIC FOR PROGRAM FLOW*******/
    @Override
    public void actionPerformed(ActionEvent ae) {
        if ((Button) ae.getSource() instanceof Button) {
            if (secondsElapsed == 0) {
                tTimer = new Timer(1000, displayTime);
                tTimer.start();
            }
            Button pointToB = (Button) ae.getSource();
            int scoreMod = pointToB.getScoreMod();
            if (scoreMod != 0) {
                /*-------------------PLAY SOUNDS--------------------------*/
                if (scoreMod == 3) {
                    try {
                        new JavaSoundAudioClip(new FileInputStream(new File("tada.wav"))).play();
                    } catch (IOException ex) {
                        Logger.getLogger(JumpingButton.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (scoreMod == 1) {
                    try {
                        new JavaSoundAudioClip(new FileInputStream(new File("chime.wav"))).play();
                    } catch (IOException ex) {
                        Logger.getLogger(JumpingButton.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (scoreMod == -1) {
                    Toolkit.getDefaultToolkit().beep();
                }
                /*-----------------------END PLAY SOUNDS--------------------*/
                modPoints(scoreMod);
                pointsLabel.setText("" + points);
                if (points >= 5 && distractionEvent == false) {
                    distractionEvent = true;
                    //INTRODUCE DISTRACTION LABEL THAT MOVES ACROSS MAIN PANEL//
                    distractLabel = new JLabel("\\~~~~~~~~~~~~~~~~~~~~~WHAT IS THIS? I DONT EVEN...", new ImageIcon("nyancat.gif"), SwingConstants.RIGHT);
                    distractLabel.setFont(new Font("Calibri", Font.ITALIC, 20));
                    distractLabel.setIconTextGap(10);
                    distractLabel.setBounds(-600, 0, 600, 30);
                    panel.add(distractLabel);
                    dTimer = new Timer(30, moveDistraction);
                    dTimer.start();
                }
                if (points == 10) {
                    tTimer.stop();
                    JOptionPane.showMessageDialog(this, "Your Score is: " + secondsElapsed);
                    System.exit(0);
                } else {
                    move();
                    this.revalidate();
                }
            } else {
                return;
            }
        }
    }

    /****************SCORE HANDLING*********************/
    private void modPoints(int pointsGained) {
        points += pointsGained;
        if (points < 0) {
            points = 0;
        }
        if (points > 10) {
            points = 10;
            tTimer.stop();
        }
    }

    /**************BUTTON RELOCATION********************/
    private void move() {
        for (int i = 0; i < buttons.length; i++) {
            //moves it randomly within panel's bounds 
            //and never to a position that cuts off any buttons 
            buttons[i].setLocation(rand.nextInt((int) panel.getBounds().getMaxX() - 50), rand.nextInt((int) panel.getBounds().getMaxY() - 50));
            //cheap overlap resolving
            for (int j = 0; j < buttons.length; j++) {
                if (j != i) {
                    while (buttons[i].getBounds().intersects(buttons[j].getBounds())) {
                        buttons[i].setLocation(rand.nextInt((int) panel.getBounds().getMaxX() - 50), rand.nextInt((int) panel.getBounds().getMaxY() - 50));
                    }
                }
            }
        }
    }
    /****************AUTOMATE BUTTON JUMP MOTION********/
    private Action buttonsJump = new AbstractAction() {

        boolean atPeak = false;
        int velocity = 12; //lazy dirty physics ^-^;

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!atPeak) {
                for (Button b : buttons) {
                    b.setLocation(b.getX(), b.getY() - velocity);
                }
                velocity -= 2;
                if (velocity == 0) {
                    velocity = 0;
                    atPeak = !atPeak;
                }
            } else {
                for (Button b : buttons) {
                    b.setLocation(b.getX(), b.getY() + velocity);
                }
                velocity += 2;
                if (velocity == 14) {
                    velocity = 12;
                    atPeak = !atPeak;
                }
            }
        }
    };
    /******************DISTRACTION EVENT****************/
    private Action moveDistraction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (distractLabel.getX() > panel.getWidth()) {
                dTimer.stop();
            } else {
                distractLabel.setLocation(distractLabel.getX() + 5, distractLabel.getY());
            }
        }
    };
    /*------------BACKGROUND COLOR CHANGER-------------*/
    private Action changeColor = new AbstractAction() {

        boolean increase = false;
        int count = 20;
        int i = 0;

        @Override
        public void actionPerformed(ActionEvent ae) {
            //moves the color by 30 increments before going the other direction
            if (i <= count) {
                if (increase == false) {
                    panel.setBackground(new Color(
                            panel.getBackground().getRed() - 5,
                            panel.getBackground().getGreen() - 3,
                            panel.getBackground().getBlue() - 2));
                } else {
                    panel.setBackground(new Color(
                            panel.getBackground().getRed() + 5,
                            panel.getBackground().getGreen() + 3,
                            panel.getBackground().getBlue() + 2));
                }
                i++;
            } else {
                i = 0; //reset
                increase = !increase; //change orientation
            }
        }
    };
    /*****************DISPLAY TIME**********************/
    private Action displayTime = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent ae) {
            secondsElapsed++;
            timerLabel.setText(secondsElapsed + "");
        }
    };
}
