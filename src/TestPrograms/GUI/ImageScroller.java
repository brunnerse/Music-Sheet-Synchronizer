package TestPrograms.GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

//
//TODO: When decreasing image size, make sure the image still is in the borders
//TODO: Try to get JScrollbar to update when its values change. 

@SuppressWarnings("serial")
public class ImageScroller extends JPanel implements AdjustmentListener, KeyListener, MouseWheelListener {

	private JScrollBar vertScrollBar, horiScrollBar;
	private MultiImagePanel imgPanel;

	private final int scrollBarSize = 16;

	public ImageScroller(int width, int height, boolean allowMinimizing) {
		super();
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(new BorderLayout());

		horiScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 150, 100, 0, 300);
		vertScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 200, 0, 1000);
		imgPanel = new MultiImagePanel(width - scrollBarSize, height - scrollBarSize, allowMinimizing ? 0f : 1f);



		this.add(horiScrollBar, BorderLayout.SOUTH);
		this.add(vertScrollBar, BorderLayout.EAST);
		this.add(imgPanel, BorderLayout.CENTER);

        horiScrollBar.addAdjustmentListener(this);
        vertScrollBar.addAdjustmentListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(this);

        imgPanel.addMouseWheelListener(this);
        imgPanel.addKeyListener(this);
        updateScrollBarDimensions();

	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

    private class MultiImagePanel extends JPanel {

		private ArrayList<BufferedImage> images = new ArrayList<>();
		private float scaleFactor = 1;
		private int upperYVal = 0, xOffset = 0;
		private int dstAllSize = 0;
		private float minScale;

		public MultiImagePanel(int width, int height, float minScale) {
			this.setPreferredSize(new Dimension(width, height));
			this.minScale = minScale;
			this.setFocusable(true);
			this.requestFocus();
		}

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			int scaledUpperYVal = (int) (upperYVal * scaleFactor);
			int yPos = 0;
			
			 Graphics2D g2d = (Graphics2D) g;

			 g2d.setRenderingHint(
                     RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);

			 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			 g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
					 RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			 
			for (BufferedImage img : images) {
				int dy2 = yPos + img.getHeight() * (int) (this.getWidth() * scaleFactor) / img.getWidth();
				
				g2d.drawImage(img, (int) (this.getWidth() * (1f - scaleFactor)) / 2 - xOffset, yPos - scaledUpperYVal,
						(int) (this.getWidth() * (1f + scaleFactor)) / 2 - xOffset, dy2 - scaledUpperYVal, 0, 0,
						img.getWidth(), img.getHeight(), null);
				yPos = dy2;
			}
			dstAllSize = yPos;

		}

		public void addImage(BufferedImage img) {
			this.addImage(img, images.size());
		}

		public void addImage(BufferedImage img, int pos) {
			images.add(pos, img);
			repaint();
		}

		public void scale(float factor) {
			if (factor >= minScale) {
				if (factor < scaleFactor) {
					// Test if pic is out of center at the bottom
					boolean fillsScreen = false;
					int scaledUpperYVal = (int) (upperYVal * factor);
					int yPos = -scaledUpperYVal;
					for (int i = 0; i < images.size(); ++i) {
						yPos += images.get(i).getHeight() * (int) (this.getWidth() * factor) / images.get(i).getWidth();
						if (yPos >= this.getWidth()) {
							fillsScreen = true;
							break;
						}
					}
					if (!fillsScreen) {
						this.setUpperYValue((int) ((yPos + scaledUpperYVal - this.getHeight()) / factor));
					}
					
					// Center pic if it's too small to fit the screen
					if (factor < 1) {
						xOffset = 0;
					} else {
						// Test if pic is out of center left or right side
						if (this.getWidth() * (1f - factor) / 2 - xOffset > 0) {
							xOffset = (int) (this.getWidth() * (1f - factor) / 2);
						} else if (this.getWidth() * (1f + factor) / 2 - xOffset < this.getWidth()) {
							xOffset = (int) (this.getWidth() * (1f + factor) / 2 - this.getWidth());
						}
					}
				}

				this.scaleFactor = factor;
				repaint();
			}
		}

		// Function used to scroll through the images vertically.
		public void setUpperYValue(int y) {
		    y = Math.max(0, Math.min(y, (int)((dstAllSize - this.getHeight()) / scaleFactor)));
		    this.upperYVal = y;
		    repaint();

		}

		// Function used to scroll through the images horizontally. The XOffset 0 means the image is aligned in the center.
		public void setXOffset(int x) {
		    x = (int)Math.max(this.getWidth() * (1f - scaleFactor) / 2, Math.min(x, this.getWidth() * (scaleFactor -1f) / 2));
		    this.xOffset = x;
		    repaint();

		}

		public int getMaximumUpperY() {
			return (int) Math.max(0f, (dstAllSize - this.getHeight()) / scaleFactor);
		}

		public int getUpperYVal() {
		    return this.upperYVal;
        }
        public int getXOffset() {
		    return this.xOffset;
        }

		public int getXCap() {
			return (int) Math.max(0f, (this.getWidth() * (scaleFactor - 1) / 2));
		}

		public float getScale() {
			return this.scaleFactor;
		}

	}

	public void scaleImages(float factor) {
		this.imgPanel.scale(factor);
	}

	public void addImage(BufferedImage img) {
		this.imgPanel.addImage(img);
	}

	public void addImage(BufferedImage img, int pos) {
		this.imgPanel.addImage(img, pos);
	}

	public void updateScrollBarPositions() {
	    horiScrollBar.setValue(imgPanel.getXOffset());
	    vertScrollBar.setValue(imgPanel.getUpperYVal());
    }

	public void updateScrollBarDimensions() {
		int maximum = this.imgPanel.getXCap();
		horiScrollBar.setMinimum(-maximum);
		int visibleAmount = (int) (maximum * 2 / this.imgPanel.getScale());
		horiScrollBar.setMaximum(maximum + visibleAmount);
		horiScrollBar.setVisibleAmount(visibleAmount);

		maximum = this.imgPanel.getMaximumUpperY();
		// vertScrollBar.setMinimum(0); This is always assumed
		visibleAmount = maximum > 0 ? imgPanel.getHeight() * imgPanel.getHeight() / maximum : 0;
		vertScrollBar.setMaximum(maximum + visibleAmount);
		vertScrollBar.setVisibleAmount(visibleAmount);
		updateScrollBarPositions();
        vertScrollBar.invalidate();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == horiScrollBar) {
			imgPanel.setXOffset(horiScrollBar.getValue()); // horiScrollBar.getMinimum() is assumed zero
		} else if (e.getSource() == vertScrollBar) {
			imgPanel.setUpperYValue(e.getValue());
		}
		updateScrollBarDimensions();
	}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
	    final int factor = 20;
        imgPanel.setUpperYValue(imgPanel.getUpperYVal() + e.getUnitsToScroll() * factor);
        updateScrollBarPositions();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int moveAmount = 50;
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                imgPanel.setUpperYValue(imgPanel.getUpperYVal() - moveAmount);
                break;
            case KeyEvent.VK_DOWN:
                imgPanel.setUpperYValue(imgPanel.getUpperYVal() + moveAmount);
                break;
            case KeyEvent.VK_LEFT:
                imgPanel.setXOffset(imgPanel.getXOffset() - moveAmount);
                break;
            case KeyEvent.VK_RIGHT:
                imgPanel.setXOffset(imgPanel.getXOffset() + moveAmount);
                break;
        }
        updateScrollBarPositions();
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

}
