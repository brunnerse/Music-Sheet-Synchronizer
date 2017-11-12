package TestPrograms.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;


//
//TODO: When decreasing image size, make sure the image still is in the borders
//TODO: Try to get JScrollbar to update when its values change. 

@SuppressWarnings("serial")
public class ImageScroller extends JPanel implements AdjustmentListener {

	private JScrollBar vertScrollBar, horiScrollBar;
	private MultiImagePanel imgPanel;
	
	private final int scrollBarSize = 16;
	
	
	public ImageScroller(int width, int height, boolean allowMinimizing) {
		super();
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(new BorderLayout());
		
		horiScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 150, 100, 0, 300);
		vertScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 200, 0, 1000);
		imgPanel = new MultiImagePanel(width - scrollBarSize, height - scrollBarSize, 
				allowMinimizing ? 0f : 1f);
		
		horiScrollBar.addAdjustmentListener(this);
		vertScrollBar.addAdjustmentListener(this);
		
		
		this.add(horiScrollBar, BorderLayout.SOUTH);
		this.add(vertScrollBar, BorderLayout.EAST);
		this.add(imgPanel, BorderLayout.CENTER);
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
		
		
		public MultiImagePanel(int width, int height) {
			this(width, height, 0f);
		}
		
		public MultiImagePanel(int width, int height, float minScale) {
			this.setPreferredSize(new Dimension(width, height));
			this.minScale = minScale;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0,  0,  this.getWidth(),  this.getHeight());
			
			
			int scaledUpperYVal = (int)(upperYVal * scaleFactor);
			int yPos = 0;
			for (BufferedImage img : images) {
				int dy2 = yPos + img.getHeight() * (int)(this.getWidth() * scaleFactor) / img.getWidth();
				g.drawImage(img, (int)(this.getWidth() * (1f - scaleFactor)) / 2 - xOffset, yPos - scaledUpperYVal,
						(int)(this.getWidth() * (1f + scaleFactor)) / 2 - xOffset, dy2 - scaledUpperYVal,
						0, 0, img.getWidth(), img.getHeight(), null);
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
				this.scaleFactor = factor;
				repaint();
			}
		}
		//Function used to scroll through the images vertically.
		public void setUpperYValue(int y) {
			if (0 <= y && (y < this.upperYVal || y <= (dstAllSize  - this.getHeight()) / scaleFactor)) {
				this.upperYVal = y;
				repaint();
			}
		}
		
		//Function used to scroll through the images horizontally.
		public void setXOffset(int x) {
			if ( Math.abs(x) < Math.abs(xOffset) || ( this.getWidth() * (1f - scaleFactor) <= 2 * x &&
					this.getWidth() * (scaleFactor - 1f) >= 2 * x )	) {
				this.xOffset = x;
				repaint();
			}
		}
		
		public int getMaximumUpperY() {
			return (int)Math.max(0f, (dstAllSize - this.getHeight()) / scaleFactor);
		}
		
		public int getXCap() {
			return (int)Math.max(0f, (this.getWidth() * (scaleFactor -1) / 2));
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
	
	public void setScrollBarDimensions() {
		int maximum = this.imgPanel.getXCap();
		horiScrollBar.setMinimum(- maximum);
		int visibleAmount = (int)(maximum * 2 / this.imgPanel.getScale());
		horiScrollBar.setMaximum(maximum + visibleAmount);
		horiScrollBar.setVisibleAmount(visibleAmount);
		
		maximum = this.imgPanel.getMaximumUpperY();
		//vertScrollBar.setMinimum(0); This is always assumed
		visibleAmount = maximum > 0 ? imgPanel.getHeight() * imgPanel.getHeight() / maximum : 0;		
		vertScrollBar.setMaximum(maximum + visibleAmount);
		vertScrollBar.setVisibleAmount(visibleAmount);
	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == horiScrollBar) {
			imgPanel.setXOffset(horiScrollBar.getValue()); //horiScrollBar.getMinimum() is assumed zero 
		} else if (e.getSource() == vertScrollBar) {
			imgPanel.setUpperYValue(e.getValue());
		}
		
		setScrollBarDimensions();
	}
	
	
}
