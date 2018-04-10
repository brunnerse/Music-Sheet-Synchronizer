package Music;

import Tools.DebuggingTools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

abstract class SheetAnalyser {
	private static final int blackMaxValue = 100;
	//findLine() will go down from the x Coordinate width/lineSearchPart to find a new Line
	private static final int lineSearchPart = 5;


	private static boolean isBlack(Color c) {
		return c.getBlue() <= blackMaxValue && c.getRed() <= blackMaxValue &&
				c.getGreen() <= blackMaxValue;
	}
	private static boolean isBlack(int RGB) {
		return (RGB & 0xff) <= blackMaxValue && (RGB & 0xff00) >> 8 <= blackMaxValue &&
				 (RGB & 0xff0000) >> 16 <= blackMaxValue;
	}
	private static void markPoint(BufferedImage img, Point p, Color c) {markPoint(img, p.x, p.y, c);}
	private static void markPoint(BufferedImage img, int x, int y, Color c) {
		final int dotRadius = 8;
		if (x - dotRadius < 0 || x + dotRadius > img.getWidth() || y - dotRadius < 0 || y + dotRadius > img.getHeight())
			return;
		for (int posY = 0; posY < dotRadius; ++posY) {
			int maxX = (int)(Math.sin(Math.acos((double)posY / dotRadius)) * dotRadius);
			for (int posX = 0; posX <= maxX; ++posX) {
				img.setRGB(x + posX, y + posY, c.getRGB());
				img.setRGB(x + posX, y - posY, c.getRGB());
				img.setRGB(x - posX, y + posY, c.getRGB());
				img.setRGB(x - posX, y - posY, c.getRGB());
			}
		}
	}

	public static void analyse(MusicSheet sheet) {
		markPoint(sheet.getImages()[0], 500, 500, Color.green);
		for (BufferedImage i : sheet.getImages()) {
			analyse(i);
		}

	}

	private static void analyse(BufferedImage i) {
		straightenImgUp(i);
		ArrayList<Line> lines = new ArrayList<Line>();
		int startY = 0;
		Line line;
		while ( (line = findLine(i, startY)) != null)
			lines.add(line);

		for (Line l : lines) {
			for(int x = 0; x < 5; ++x) {
				markPoint(i, l.startPoints[x], Color.green);
				markPoint(i, l.endPoints[x], Color.green);
			}
		}
	}

	private static void straightenImgUp(BufferedImage i) {
		int xVar = i.getWidth() / lineSearchPart;
		int yVar = 0;
		Point[] dots = new Point[5];
		//Index in the dots array that points to the last added point.
		int idx = -1;
		//find 5 dots above each other in the same margin
		do {
			if(isBlack(i.getRGB(xVar, yVar))) {
				dots[++idx] = new Point(xVar, yVar);
				while (isBlack(i.getRGB(xVar, ++yVar)))
					;
			}
			//if you got 5 Points, test if Points have approximately the same margin to each other
			if (idx == 4) {
				for (Point p : dots)
					markPoint(i, p, Color.cyan);
				int margin = dots[1].y - dots[0].y;
				for (int a = 1; a < idx; ++a) {
					//test if the margin isn't the same for 0.5cm, if so, delete first dot and continue
					if (Math.abs(dots[a + 1].y - dots[a].y) - margin > 0.5 * getDotsPerCm(i)) {
						DebuggingTools.printArray(dots);
						for(a = 0; a < idx; ++a) {
							System.out.println(a + "\t" + idx);
							dots[a] = dots[a + 1];
						}
						DebuggingTools.printArray(dots);
						--idx;
						break;
					}
				}
			}

			yVar++;
			if (yVar >= i.getHeight()) {
				yVar = 0;
				xVar += getDotsPerCm(i);
				idx = -1;
				if (xVar >= i.getWidth())
					return;
			}
			if (yVar % 50 == 0) {
				markPoint(i, xVar, yVar, Color.magenta);
				System.out.println("Current pos: " + yVar + "\t" + idx);
			}

		} while (idx < dots.length - 1);  //Abort if array is full

		System.out.println("Lighting points up..");
		for (Point p : dots)
			markPoint(i, p, Color.red);

	}

	private static Line findLine(BufferedImage i, int yStart) {
		return findLine(i, i.getWidth() / lineSearchPart, yStart);
	}
	private static Line findLine(BufferedImage i) {
		return findLine(i, i.getWidth() / lineSearchPart, 0);
	}

	private static Line findLine(BufferedImage i, int xStart, int yStart) {

		return null;
	}

	//Image is assumed to be Din A4 in Height
	public static int getDotsPerCm(BufferedImage i) {
		return (int)(i.getHeight() / 29.7);
	}
	
	/**
	 * Represents a Line in the Music Sheet
	 * which is 5 Lines under each other;
	 */
	private static class Line {
		private BufferedImage img;
		private Point startPoints[], endPoints[];
		private int thickness = 0;

		public Line(BufferedImage i, Point startPoints[], Point endPoints[]) {
			if (startPoints.length != 5 || endPoints.length != 5)
				throw new IllegalArgumentException("Arrays need 5 Points each");
			this.img = i; this.startPoints = startPoints; this.endPoints = endPoints;
		}

		public Line(BufferedImage i, Point s1, Point s2, Point s3, Point s4, Point s5, int xEndVal) {
			this.img = i;
			startPoints = new Point[] {s1, s2, s3, s4, s5};
			endPoints = new Point[5];
			for (int x = 0; x < 5; ++x)
				endPoints[x] = new Point(xEndVal, (int)startPoints[x].getY());
		}
		public Line(BufferedImage i, Point s1, Point s2, Point s3, Point s4, Point s5,
					Point e1, Point e2, Point e3, Point e4, Point e5) {
			this.img = i;
			startPoints = new Point[] {s1, s2, s3, s4, s5};
			endPoints = new Point[] {e1, e2, e3, e4, e5};
		}

		public BufferedImage getImage() {
			return img;
		}
		public Point[] getStartPoints() {
			return startPoints;
		}
		public Point[] getEndPoints() {
			return endPoints;
		}

		//returns the biggest Thickness of one of the 5 lines.
		public int getThickness() {
			if (thickness == 0) { //thickness not yet calculated
				for (Point p : startPoints) {
					p.x += getDotsPerCm(img);
					int y = p.y;
					while (isBlack(img.getRGB(p.x, y)))
						--y;
					int upperY = y++;
					while (isBlack(img.getRGB(p.x, y))) {
						y++;
					}
					this.thickness = Math.max(thickness, y - 1 - upperY);
				}
			}
			return thickness;
		}

	}

}
