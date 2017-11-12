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
//TODO: Change Scrollbar dimensions depending on scaleFactor
//TODO: Cap upperYVal and xOffset values so the image always fills to the borders

@SuppressWarnings("serial")
public class ImageScroller extends JPanel implements AdjustmentListener {

	private JScrollBar vertScrollBar, horiScrollBar;
	private MultiImagePanel imgPanel;
	
	private final int scrollBarSize = 16;
	
	public ImageScroller(int width, int height) {
		super();
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(new BorderLayout());
		
		horiScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 500, 50, 0, 1000);
		vertScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 100, 0, 2000);
		imgPanel = new MultiImagePanel(width - scrollBarSize, height - scrollBarSize);
		
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
		
		
		public MultiImagePanel(int width, int height) {
			this.setPreferredSize(new Dimension(width, height));
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0,  0,  this.getWidth(),  this.getHeight());
			
			
			int scaledUpperYVal = (int)(upperYVal * scaleFactor);
			int yPos = 0;
			for (BufferedImage img : images) {
				int dy2 = yPos + img.getHeight() * (int)(this.getWidth() * scaleFactor) / img.getWidth();
				g.drawImage(img, (this.getWidth() - (int)(this.getWidth() * scaleFactor)) / 2 - xOffset, yPos - scaledUpperYVal,
						(this.getWidth() + (int)(this.getWidth() * scaleFactor)) / 2 - xOffset, dy2 - scaledUpperYVal,
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
			if (factor >= 1) {
				this.scaleFactor = factor;
				repaint();
			}
		}
		//Function used to scroll through the images vertically.
		public void setUpperYValue(int y) {
			if (0 <= y && y <= dstAllSize - this.getHeight()) {
				this.upperYVal = y;
				repaint();
			}
		}
		
		//Function used to scroll through the images horizontally.
		public void setXOffset(int x) {
			if (true) {
				this.xOffset = x;
				repaint();
			}
		}
		
		public int getUpperYCap() {
			return Math.min(0, dstAllSize - this.getHeight());
		}
		
		public int getXCap() {
			return (int)(this.getWidth() * (1f - scaleFactor) / 2);
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
		//horiScrollBar.setMaximum(this.imgPanel.getXCap());
		horiScrollBar.setVisibleAmount(20);
		
		
	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == horiScrollBar) {
			imgPanel.setXOffset(horiScrollBar.getValue() - horiScrollBar.getMaximum() / 2); //horiScrollBar.getMinimum() is assumed zero 
		} else if (e.getSource() == vertScrollBar) {
			imgPanel.setUpperYValue(e.getValue());
		}
		
		setScrollBarDimensions();
	}
	
	
}
