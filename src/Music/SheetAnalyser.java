package Music;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

abstract class SheetAnalyser {
	private static final int blackMaxValue = 100;

	public static void analyse(MusicSheet sheet) {
		markPoint(sheet.getImages()[0], 200, 200, new Color(blackMaxValue, blackMaxValue, blackMaxValue));
	}

	private static boolean isBlack(Color c) {
		return c.getBlue() <= blackMaxValue && c.getRed() <= blackMaxValue && c.getGreen() <= blackMaxValue;
	}

	private static void markPoint(BufferedImage img, int x, int y, Color c) {
		final int dotSize = 50;
		for (int i = -dotSize; i < dotSize; ++i) {
			for (int h = -dotSize; h < dotSize; ++h)
				img.setRGB(x + h, y + i, c.getRGB());
		}
		System.out.println("set img colors.");
	}

	
	/**
	 * Represents a Line in the Music Sheet
	 *by giving the img, the x1 start coordinate, the x2 end coordinate, and y1, y2 coordinates.
	 *These coordinates point to the Line ends.
	 */
	private static class Line {
		public BufferedImage[] img;
		public int x1, x2, y1, y2;
		
		public ArrayList<Voice> voices = new ArrayList<Voice>();
		
		
	}
	
	/**
	 * represents a Voice inside of a Line.
	 *
	 */
	private static class Voice {
		
	}
}
