package TestPrograms;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class ImageShower extends JFrame {

	private ImagePanel imgPanel;
	
	public static void main(String []args) {
		new ImageShower();
	}
	
	public ImageShower() {
		super("Image Testing");
		try {
			imgPanel = new ImagePanel(ImageIO.read(new File("dust2.png")), 800, 800);
	
		} catch (IOException e) {
			System.err.println("ERROR: Can't find image dust2.png");
			return;
		}
		
		this.add(imgPanel);
		
		this.setSize(800, 800);
		this.setVisible(true);
		
	}
	
	private class MouseDragManager implements MouseMotionListener, MouseListener {

		private int oldXPos, oldYPos;
		@Override
		public void mousePressed(MouseEvent e) {
			oldXPos = e.getX();
			oldYPos = e.getY();

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			
		}
		
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent arg0) {}
		public void mouseMoved(MouseEvent e) {}
	}
	
	private class ImagePanel extends JPanel {
		BufferedImage img;
		int dstx1, dsty1, dstx2, dsty2, srcx1, srcy1, srcx2, srcy2;
		
		public ImagePanel(BufferedImage img, int width, int height) {
			super();
			this.img = img;
			dstx1 = dsty1 = 0; 
			srcx1 = srcy1 = 0;
			dstx2 = width - dstx1;
			dsty2 = height - dsty1;
			srcx2 = img.getWidth();
			srcy2 = img.getHeight();
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					dstx2 = getWidth();
					dsty2 = getHeight();
				}
			});
			return;
		}


		public BufferedImage getImg() {
			return img;
		}
		public int getDstx1() {
			return dstx1;
		}
		public int getDsty1() {
			return dsty1;
		}
		public int getDstx2() {
			return dstx2;
		}
		public int getDsty2() {
			return dsty2;
		}
		public int getSrcx1() {
			return srcx1;
		}
		public int getSrcy1() {
			return srcy1;
		}
		public int getSrcx2() {
			return srcx2;
		}
		public int getSrcy2() {
			return srcy2;
		}
		
		public void setImg(BufferedImage img) {
			this.img = img;
		}
		public void setDstx1(int dstx1) {
			this.dstx1 = dstx1;
		}
		public void setDsty1(int dsty1) {
			this.dsty1 = dsty1;
		}
		public void setDstx2(int dstx2) {
			this.dstx2 = dstx2;
		}
		public void setDsty2(int dsty2) {
			this.dsty2 = dsty2;
		}
		public void setSrcx1(int srcx1) {
			this.srcx1 = srcx1;
		}
		public void setSrcy1(int srcy1) {
			this.srcy1 = srcy1;
		}
		public void setSrcx2(int srcx2) {
			this.srcx2 = srcx2;
		}
		public void setSrcy2(int srcy2) {
			this.srcy2 = srcy2;
		}

		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(img, dstx1, dsty1, dstx2, dsty2, srcx1, srcy1,
					srcx2, srcy2, null);
		}
	}
	
	
}
