package TestPrograms.GUI;

import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {
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
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, dstx1, dsty1, dstx2, dsty2, srcx1, srcy1,
				srcx2, srcy2, null);
	}
	
	public void setPosX(int x) {
		this.dstx2 = dstx2 - dstx1 + x;
		this.dstx1 = x;
	}
	
	public void setPosY(int y) {
		this.dsty2 = dsty2 - dsty1 + y;
		this.dsty1 = y;
	}
	
	public void setHeight(int height) {
		this.dsty2 = this.dsty1 + height;
	}
	
	public void setWidth(int width) {
		this.dstx2 = this.dstx1 + width;
	}
	
	public boolean isInXRange(int i) {
		return (i >= 0 && i <= img.getWidth());
	}
	
	public boolean isInYRange(int i) {
		return (i >= 0 && i <= img.getHeight());
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
		repaint();
	}
	public void setDstx1(int dstx1) {
			this.dstx1 = dstx1;
		repaint();
	}
	public void setDsty1(int dsty1) {
		this.dsty1 = dsty1;
		repaint();
	}
	public void setDstx2(int dstx2) {
		this.dstx2 = dstx2;
		repaint();
	}
	public void setDsty2(int dsty2) {
		this.dsty2 = dsty2;
		repaint();
	}
	public void setSrcx1(int srcx1) {
		this.srcx1 = srcx1;
		repaint();
	}
	public void setSrcy1(int srcy1) {
		this.srcy1 = srcy1;
		repaint();
	}
	public void setSrcx2(int srcx2) {
		this.srcx2 = srcx2;
		repaint();
	}
	public void setSrcy2(int srcy2) {
		this.srcy2 = srcy2;
		repaint();
	}
}