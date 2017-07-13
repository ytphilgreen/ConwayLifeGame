package edu.cnm.deepdive.life;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Entry point and controller (in the MVC sense) for a basic implementation of
 * the well-known <em>cellular automaton</em> (CA), Conway's "Game of Life".
 *  
 * @author Nicholas Bennett
 */
public class Player implements ActionListener {

  private static final String WINDOW_TITLE = "Deep Dive: Conway's Game of Life";
  private static final String THRESHOLD_LABEL = "Density";
  private static final String SETUP_BUTTON = "Reset";
  private static final String STEP_BUTTON = "Step";
  private static final String RUN_BUTTON_UNSELECTED = "Run!";
  private static final String RUN_BUTTON_SELECTED = "Stop!";
  private static final int WIDTH = 150;
  private static final int HEIGHT = 150;
  private static final float SCALE = 6f;
  private static final int PADDING = 10;
  private static final double DEFAULT_DENSITY = 0.15;
  
  private Surface surface;
  private Life life;

  JSlider thresholdSlider;
  JButton setup;
  JButton step;
  JToggleButton run;

  private boolean uiSetup = false;
  private boolean populating = false;
  private boolean stepping = false;
  private boolean running = false;
  private double density = DEFAULT_DENSITY;
  
  /**
   * Creates and starts an instance of the {@code Player} class, to control
   * execution of <em>Life</em>.
   * 
   * @param args  Command line arguments (ignored)
   */
  public static void main(String[] args) {
    Player player = new Player();
    player.init();
    player.run();
  }

  /**
   * Handles button-clicks to single-step, start, stop, and reset the Life CA.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == setup) {
      setup.setEnabled(false);
      step.setEnabled(false);
      run.setEnabled(false);
      synchronized (this) {
        populating = true;
        notify();
      }
      run.setEnabled(true);
      step.setEnabled(true);
      setup.setEnabled(true);
    } else if (source == step) {
      setup.setEnabled(false);
      step.setEnabled(false);
      run.setEnabled(false);
      synchronized (this) {
        stepping = true;
        notify();
      }
      run.setEnabled(true);
      step.setEnabled(true);
      setup.setEnabled(true);
    } else if (source == run) {
      if (run.isSelected()) {
        setup.setEnabled(false);
        step.setEnabled(false);
        run.setText(RUN_BUTTON_SELECTED);
        synchronized (this) {
          running = true;
          notify();
        }
      } else {
        synchronized (this) {
          running = false;
          notify();
        }
        run.setText(RUN_BUTTON_UNSELECTED);
        step.setEnabled(true);
        setup.setEnabled(true);
      }      
    }
  }
  
  private void init() {
    life = new Life(WIDTH, HEIGHT);
    life.populate(density);
    SwingUtilities.invokeLater(() -> buildGui());
    synchronized (this) {
      while (!uiSetup) {
        try {
          wait();
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      }
    }
  }
  
  private void buildGui() {
    JFrame frame = new JFrame(WINDOW_TITLE);
    frame.setLayout(new BorderLayout());
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    surface = new Surface(WIDTH, HEIGHT, SCALE);
    surface.setField(life.getField());
    frame.add(surface, BorderLayout.NORTH);

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 2 * PADDING, 0));
    controls.setBorder(new CompoundBorder(
        LineBorder.createGrayLineBorder(), 
        new EmptyBorder(PADDING, PADDING, PADDING, PADDING)));

    JLabel thresholdLabel = new JLabel(THRESHOLD_LABEL); 
    thresholdSlider = new JSlider(0, 50, (int) Math.round(DEFAULT_DENSITY * 100));    
    setup = new JButton(SETUP_BUTTON);
    step = new JButton(STEP_BUTTON);
    run = new JToggleButton(RUN_BUTTON_UNSELECTED);
    setup.addActionListener(this);
    step.addActionListener(this);
    run.addActionListener(this);
    JPanel thresholdPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PADDING, 0));
    thresholdPanel.add(thresholdLabel);
    thresholdPanel.add(thresholdSlider);
    controls.add(thresholdPanel);
    controls.add(setup);
    controls.add(step);
    controls.add(run);
    frame.add(controls, BorderLayout.SOUTH);
    
    frame.pack();
    frame.setVisible(true);
    synchronized (this) {
      uiSetup = true;
      notify();
    }
  }
  
  private synchronized void run() {
    while (true) {
      if (populating) {
        populating = false;
        life.populate(density);
        surface.setField(life.getField());
        try {
          wait();
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      } else if (running) {
        step();
        try {
          wait(100);
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      } else if (stepping) {
        stepping = false;
        step();
        try {
          wait();
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      } else {
        try {
          wait();
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      }
    }
  }
  
  private void step() {
    life.step();
    surface.setField(life.getField());
  }

}
