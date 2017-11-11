package TestPrograms.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

@SuppressWarnings("serial")
public class ImageScroller extends JPanel implements AdjustmentListener {

	private JScrollBar vertScrollBar, horiScrollBar;
	private MultiImagePanel imgPanel;
	
	private final int scrollBarSize = 10;
	
	public static void main(String[] args) throws Exception {
		JFrame f = new JFrame("ImageScroller");

		ImageScroller is = new ImageScroller(600, 600);
		is.addImage(ImageIO.read(new File("alyssa arce.jpg")));
		is.addImage(ImageIO.read(new File("emily.jpg")));
		
		JScrollBar scaleScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 50, 100, 300);
		scaleScrollBar.addAdjustmentListener( (AdjustmentEvent e) -> is.scaleImages(e.getValue() / 100f) );
		
		f.add(scaleScrollBar, BorderLayout.NORTH);
		f.add(is, BorderLayout.SOUTH);

		f.pack();
		f.setVisible(true);
		System.out.println(f.getWidth() + "\t" + f.getHeight());
		System.out.println(is.getWidth() + "\t" + is.getHeight());
	}
	
	
	public ImageScroller(int width, int height) {
		super();
		this.setPreferredSize(new Dimension(width, height));
		this.setLayout(new BorderLayout());
		
		horiScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 20, 0, 100);
		vertScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 20, 0, 100);
		imgPanel = new MultiImagePanel(width - scrollBarSize, height - scrollBarSize);
		
		this.add(horiScrollBar, BorderLayout.SOUTH);
		this.add(vertScrollBar, BorderLayout.EAST);
		this.add(imgPanel, BorderLayout.CENTER);
		
		//scrollBar[0].setSize(width - scrollBarSize, scrollBarSize);
		//scrollBar[1].setSize(scrollBarSize, height);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
	}
	
	private class MultiImagePanel extends JPanel {
		
		private ArrayList<BufferedImage> images = new ArrayList<>();
		private float scaleFactor = 1;
		private int upperYVal = 0, upperXVal = 0;
		
		public MultiImagePanel(int width, int height) {
			this.setPreferredSize(new Dimension(width, height));
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0,  0,  this.getWidth(),  this.getHeight());
			
			int yPos = 0;
			for (BufferedImage img : images) {
				int dy2 = yPos + img.getHeight() * this.getWidth() / img.getWidth();
				int srcWidth = (int)(img.getWidth() / scaleFactor);
				int srcHeight = (int)(img.getHeight() / scaleFactor);
				g.drawImage(img, 0, yPos, this.getWidth(), dy2, (img.getWidth() - srcWidth) / 2,
						(img.getHeight() - srcHeight) / 2, (img.getWidth() + srcWidth) / 2, (img.getHeight() + srcHeight) / 2, null);
				yPos = dy2 + 1;
			}
		}
		
		public void addImage(BufferedImage img) {
			this.addImage(img, images.size());
		}
		
		public void addImage(BufferedImage img, int pos) {
			images.add(pos, img);
		}
		
		public void scale(float factor) {
			if (factor >= 1) {
				this.scaleFactor = factor;
				repaint();
			}
		}
		//Function used to scroll through the images vertically.
		public void setUpperYValue(int y) {
			if (y >= 0)
				this.upperYVal = y;
		}
		
		//Function used to scroll through the images horizontally.
		public void setUpperXValue(int x) {
			if (x >= 0)
				this.upperXVal = x;
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


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == horiScrollBar) {
			imgPanel.setUpperXValue(e.getValue());
		} else if (e.getSource() == vertScrollBar) {
			imgPanel.setUpperYValue(e.getValue());
		}
	}
	
	
}
