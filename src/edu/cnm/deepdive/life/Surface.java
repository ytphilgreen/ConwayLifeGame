/**
 * 
 */
package edu.cnm.deepdive.life;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;


/**
 * @author Yolanda Philgreen
 *
 */
public class Surface extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 2495214640084762801L;

  private static final int INSET = 1;
  private static final Color CELL_COLOR = Color.RED;
  
  public final int width;
  public final int height;
  public final float scale;
  
  private boolean [][] field;
  
  
  public Surface(int width, int height, float scale) {
    super(true);
    this.width = width;
    this.height = height;
    this.scale = scale;
    setBorder(LineBorder.createGrayLineBorder());
  }


  /* (non-Javadoc)
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g) {
    setBackground(Color.BLUE);
    super.paintComponent(g);
    g.setColor(CELL_COLOR);
    synchronized (this) {
      for (int i = 0; i < height; i++) {
      int top = INSET + Math.round(i * scale);
      int height = INSET + Math.round((i + 1)* scale) - top;
      for (int j = 0; j < width; j++) {
        int left = INSET + Math.round(j* scale);
        int width = INSET + Math.round((j + 1) * scale) - left;
        if (field[i] [j]) {
          g.fillOval(left, top, width, height);
        }
       }
      }
    }
  }


  /* (non-Javadoc)
   * @see javax.swing.JComponent#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {

    return new Dimension(1 + 2 * INSET + Math.round(width * scale),
    1 + 2 * INSET + Math.round(height * scale));
    
  }
 public synchronized void setField(boolean[][] field) {
   this.field = field;
   repaint();
 }
  
}
