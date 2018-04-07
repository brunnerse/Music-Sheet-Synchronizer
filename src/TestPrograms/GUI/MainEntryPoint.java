package TestPrograms.GUI;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollBar;

import PDF.PDFExtractor;

public class MainEntryPoint {

	public static void main(String[] args) throws Exception {
		startImageScroller();

	}

	public static BufferedImage[] startImageScroller() {
		JFrame f = new JFrame("ImageScroller");

		ImageScroller is = new ImageScroller(800, 700, true);
		BufferedImage[] images;
		try {
			images = PDFExtractor.getScreenshotsFromFile("PDF-Files/Arrival to Earth.pdf");
		} catch (IOException e) {
			System.err.println("Couldnt extract images from PDF File: " + e.getMessage());
			return null;
		}
		for (BufferedImage i : images) {
			is.addImage(i);
		}
		
		
		JScrollBar scaleScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 50, 0, 300);
		scaleScrollBar.addAdjustmentListener((AdjustmentEvent e) -> is.scaleImages(e.getValue() / 100f));
		
		f.add(scaleScrollBar, BorderLayout.NORTH);
		f.add(is);
		f.pack();
		f.setVisible(true);
		
		return images;
	}
}
