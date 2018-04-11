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

    private static void markPoint(BufferedImage img, Point p, Color c) {
        markPoint(img, p.x, p.y, c);
    }

    private static void markPoint(BufferedImage img, int x, int y, Color c) {
        final int dotRadius = 8;
        int colorRGB = c.getRGB();
        if (x - dotRadius < 0 || x + dotRadius > img.getWidth() || y - dotRadius < 0 || y + dotRadius > img.getHeight())
            return;
        for (int posY = 0; posY < dotRadius; ++posY) {
            int maxX = (int) (Math.sin(Math.acos((double) posY / dotRadius)) * dotRadius);
            for (int posX = 0; posX <= maxX; ++posX) {
                img.setRGB(x + posX, y + posY, colorRGB);
                img.setRGB(x + posX, y - posY, colorRGB);
                img.setRGB(x - posX, y + posY, colorRGB);
                img.setRGB(x - posX, y - posY, colorRGB);
            }
        }

    }

    private static void markLine(BufferedImage img, Point pStart, Point pEnd, Color c) {
        markLine(img, pStart.x, pStart.y, pEnd.x, pEnd.y, c);
    }

    private static void markLine(BufferedImage img, int x1, int y1, int x2, int y2, Color c) {
        final int halfThickness = 5;
        int colorRGB = c.getRGB();

        if (x2 < x1) { //constraint:  x2 > x1: to hold it, eventually swap points
            x1 = x1 ^ x2;
            x2 = x1 ^ x2;
            x1 = x1 ^ x2;
            y1 = y1 ^ y2;
            y2 = y1 ^ y2;
            y1 = y1 ^ y2;
        }

        final double ascent = (double) (y2 - y1) / (x2 - x1);
        //System.out.println("Drawing line from " +x1 + ", " + y1 + " to " + x2 + ", " + y2 + " ascent: " + ascent);
        if (Math.abs(ascent) > 500) {
            for (int x = x1 - halfThickness; x <= x1 + halfThickness; x++) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    img.setRGB(x, y, colorRGB);
                }
            }
            return;
        }

        final double angle = Math.atan(ascent);
        double rectAngle = angle + Math.PI / 2;
        double rectSin = Math.sin(rectAngle), rectCos = Math.cos(rectAngle);

        Point startPoint1 = new Point((int) (x1 - halfThickness * rectCos),
                (int) (y1 - halfThickness * rectSin));
        Point startPoint2 = new Point((int) (x1 + halfThickness * rectCos),
                (int) (y1 + halfThickness * rectSin));
        //the Point more to the left must be startPoint1
        if (startPoint1.x > startPoint2.x) {
            Point p = startPoint1;
            startPoint1 = startPoint2;
            startPoint2 = p;
        }

        //draw Parallelogram
        for (int x = startPoint2.x; x <= x2 + startPoint1.x - x1; ++x) {
            int inc = startPoint2.y > startPoint1.y ? -1 : 1;
            for (int y = (int) (startPoint2.y + ascent * (x - startPoint2.x));
                 y != (int) (startPoint1.y + ascent * (x - startPoint1.x)); y += inc) {
                img.setRGB(x, y, colorRGB);
            }
        }

        //draw the missing triangles at each side of the line
        double upperAscent, lowerAscent;
        if (ascent < 0) {
            upperAscent = ascent;
            lowerAscent = (double)(startPoint2.y - startPoint1.y)/(startPoint2.x - startPoint1.x);
        } else {
            upperAscent = (double)(startPoint2.y - startPoint1.y) / (startPoint2.x - startPoint1.x);
            lowerAscent = ascent;
        }

        for (int x = startPoint1.x; x <= startPoint2.x; x++) {
            for (int y = (int) (startPoint1.y + (x - startPoint1.x) * upperAscent);
                 y <= (int) (startPoint1.y + (x - startPoint1.x) * lowerAscent); y++) {
                img.setRGB(x, y, colorRGB);
                img.setRGB(x2 + (x1 - x), y2 + (y1 - y), colorRGB);
            }
        }


    }

    public static void analyse(MusicSheet sheet) {
        for (BufferedImage i : sheet.getImages()) {
            analyse(i);
        }

    }

    private static void analyse(BufferedImage i) {
        straightenImgUp(i);
        /*
        ArrayList<MusicLine> lines = new ArrayList<MusicLine>();
        int startY = 0;
        MusicLine line;
        while ((line = findMusicLine(i, startY)) != null)
            lines.add(line);

        for (MusicLine l : lines) {
            for (int x = 0; x < 5; ++x) {
                markPoint(i, l.startPoints[x], Color.green);
                markPoint(i, l.endPoints[x], Color.green);
            }
        }
        */
    }

    private static void straightenImgUp(BufferedImage img) {
        int xVar = img.getWidth() / lineSearchPart;
        int yVar = 0;
        Point[] dots = new Point[5];
        //Index in the dots array that points to the last added point.
        int idx = -1;
        //find 5 dots above each other in the same margin
        do {
            if (isBlack(img.getRGB(xVar, yVar))) {
                //check if Point belongs to a line
                if (findLineFromPoint(img, new Point(xVar, yVar), getDotsPerCm(img) * 5) != null) {
                    dots[++idx] = new Point(xVar, yVar);

                    while (isBlack(img.getRGB(xVar, ++yVar)))
                        ;
                } else {
                    return; //TODO: DELETE LATER
                }
            }
            //if you got 5 Points, test if Points have approximately the same margin to each other
            if (idx == 4) {
                for (Point p : dots)
                    markPoint(img, p, Color.cyan);
                int margin = dots[1].y - dots[0].y;
                for (int a = 1; a < idx; ++a) {
                    //test if the margin isn't the same for 0.5cm, if so, delete first dot and continue
                    if (Math.abs(dots[a + 1].y - dots[a].y) - margin > 0.5 * getDotsPerCm(img)) {
                        DebuggingTools.printArray(dots);
                        for (a = 0; a < idx; ++a) {
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
            if (yVar >= img.getHeight()) {
                yVar = 0;
                xVar += getDotsPerCm(img);
                idx = -1;
                if (xVar >= img.getWidth())
                    return;
            }
            if (yVar % 50 == 0) {
                markPoint(img, xVar, yVar, Color.magenta);
                System.out.println("Current pos: " + yVar + "\t" + idx);
            }

        } while (idx < dots.length - 1);  //Abort if array is full

        System.out.println("Lighting points up..");
        markLine(img, dots[0], dots[4], Color.blue);
        for (Point p : dots)
            markPoint(img, p, Color.red);

    }

    /**
     *
     * Searches for the biggest Line that goes through the Point p. Returns null if nothing found.
     * @param minLen: If the biggest found line is smaller than minLen, function returns null
     */
    private static Line findLineFromPoint(BufferedImage img, Point p, int minLen) {
        double minAng = Math.PI / 4;
        int maxLen = 0;
        System.out.println("findLineFromPoint was called with Point " + p);
        markPoint(img, p, Color.orange);
        Line l = new Line();
        Point pLeft = new Point();
        for (Point pRight = new Point((int) (p.x + minLen * Math.cos(minAng)), (int) (p.y + minLen * Math.sin(minAng)));
             pRight.y >= (int) (p.y - minLen * Math.sin(minAng));
             pRight.y--) {
            //Calc x-Coordinate of search point dependent on the y-Coordinate
            //pRight.x = p.x + (int) (Math.cos(Math.asin((pRight.y - p.y) / minLen)) * minLen); //TODO
            //Search right side
            markPoint(img, pRight, Color.blue);
            //Search left side for Line length

            pLeft.x = 2 * p.x - pRight.x;
            pLeft.y = 2 * p.y - pRight.y;
            markPoint(img, pLeft, Color.red);
        }
        if (maxLen == 0) {//No Line with at least minLen length was found
            return null;
        }
        return l;
    }

    private static MusicLine findMusicLine(BufferedImage img, int yStart) {
        return findMusicLine(img, img.getWidth() / lineSearchPart, yStart);
    }

    private static MusicLine findMusicLine(BufferedImage img) {
        return findMusicLine(img, img.getWidth() / lineSearchPart, 0);
    }

    private static MusicLine findMusicLine(BufferedImage i, int xStart, int yStart) {

        return null;
    }

    //Image is assumed to be Din A4 in Height
    public static int getDotsPerCm(BufferedImage i) {
        return (int) (i.getHeight() / 29.7);
    }

    private static class Line {
        public static Point startPoint, endPoint;
    }

    /**
     * Represents a MusicLine in the Music Sheet
     * which is 5 MusicLines under each other;
     */
    private static class MusicLine {
        private BufferedImage img;
        private Point startPoints[], endPoints[];
        private int thickness = 0;

        public MusicLine(BufferedImage i, Point startPoints[], Point endPoints[]) {
            if (startPoints.length != 5 || endPoints.length != 5)
                throw new IllegalArgumentException("Arrays need 5 Points each");
            this.img = i;
            this.startPoints = startPoints;
            this.endPoints = endPoints;
        }

        public MusicLine(BufferedImage i, Point s1, Point s2, Point s3, Point s4, Point s5, int xEndVal) {
            this.img = i;
            startPoints = new Point[]{s1, s2, s3, s4, s5};
            endPoints = new Point[5];
            for (int x = 0; x < 5; ++x)
                endPoints[x] = new Point(xEndVal, (int) startPoints[x].getY());
        }

        public MusicLine(BufferedImage i, Point s1, Point s2, Point s3, Point s4, Point s5,
                    Point e1, Point e2, Point e3, Point e4, Point e5) {
            this.img = i;
            startPoints = new Point[]{s1, s2, s3, s4, s5};
            endPoints = new Point[]{e1, e2, e3, e4, e5};
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
