package TestPrograms.GUI;

import javax.swing.JFrame;

import java.awt.Component;
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
		BufferedImage img;
		try {
			img = ImageIO.read(new File("dust2.png"));
			imgPanel = new ImagePanel(img, 800, 800);
	
		} catch (IOException e) {
			System.err.println("ERROR: Can't find image dust2.png");
			return;
		}
		this.add(imgPanel);
		MouseDragManager m = new MouseDragManager(imgPanel);
		this.addMouseListener(m);
		this.addMouseMotionListener(m);
		for (Component c : this.getComponents()) {
			c.addMouseListener(m);
			c.addMouseMotionListener(m);
		}
		final int height = 800;
		this.setSize(height * img.getWidth() / img.getHeight(), height);
		this.setVisible(true);
		
	}
	
	private class MouseDragManager implements MouseMotionListener, MouseListener {

		private int oldXPos, oldYPos;
		private int oldSrcx1, oldSrcx2, oldSrcy1, oldSrcy2;
		private ImagePanel p;
		
		public MouseDragManager(ImagePanel imgPanel) {
			this.p = imgPanel;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			oldXPos = e.getX();
			oldYPos = e.getY();
			oldSrcx1 = p.getSrcx1();
			oldSrcx2 = p.getSrcx2();
			oldSrcy1 = p.getSrcy1();
			oldSrcy2 = p.getSrcy2();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			boolean scale = e.isMetaDown() || e.isPopupTrigger();
			if (scale) {
				if (oldXPos - getWidth() / 2 > 0) {
					p.setSrcx2(Math.min(p.getImg().getWidth(), oldSrcx2 + oldXPos - e.getX()));
				} else {
					p.setSrcx1(Math.max(0, oldSrcx1 + oldXPos - e.getX()));
				}
				if (oldYPos - getHeight() / 2 > 0) {
					p.setSrcy2(Math.min(p.getImg().getHeight(), oldSrcy2 + oldYPos - e.getY()));
				} else {
					p.setSrcy1(Math.max(0, oldSrcy1 + oldYPos - e.getY()));
					
				}
			} else {
				if (p.isInXRange(oldSrcx1 + oldXPos - e.getX()) && 
						p.isInXRange(oldSrcx2 + oldXPos - e.getX())) {
					p.setSrcx1(oldSrcx1 + oldXPos - e.getX());
					p.setSrcx2(oldSrcx2 + oldXPos - e.getX());
				}
				if (p.isInYRange(oldSrcy1 + oldYPos - e.getY()) && 
						p.isInYRange(oldSrcy2 + oldYPos - e.getY()) ) {
					p.setSrcy1(oldSrcy1 + oldYPos - e.getY());
					p.setSrcy2(oldSrcy2 + oldYPos - e.getY());
				}
			}
		}
		
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent arg0) {}
		public void mouseMoved(MouseEvent e) {}
	}

	
	
}
