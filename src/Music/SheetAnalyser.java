package Music;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

abstract class SheetAnalyser {
	private static final int blackMaxValue = 100;

	public static void analyse(MusicSheet sheet) {

		markPoint(sheet.getImages()[0], 500, 500, Color.green);
	}

	private static boolean isBlack(Color c) {
		return c.getBlue() <= blackMaxValue && c.getRed() <= blackMaxValue && c.getGreen() <= blackMaxValue;
	}

	private static void markPoint(BufferedImage img, int x, int y, Color c) {
		final int dotRadius = 15;
		for (int posY = 0; posY < dotRadius; ++posY) {
			int maxX = (int)(Math.sin(Math.acos((double)posY / dotRadius)) * dotRadius);
			for (int posX = 0; posX <= maxX; ++posX) {
				img.setRGB(x + posX, y + posY, c.getRGB());
				img.setRGB(x + posX, y - posY, c.getRGB());
				img.setRGB(x - posX, y + posY, c.getRGB());
				img.setRGB(x - posX, y - posY, c.getRGB());
			}
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
