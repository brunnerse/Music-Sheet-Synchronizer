package PDF;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.util.ArrayList;

public class PDFExtractor {

	private static ArrayList<BufferedImage> images;

	public static BufferedImage[] getScreenshotsFromFile(String fileName) throws IOException {
		return getScreenshotsFromFile(new File(fileName));
	}

	public static BufferedImage[] getScreenshotsFromFile(File file) throws IOException {
		images = new ArrayList<>();

		PDDocument document = PDDocument.load(file);

		PDFRenderer renderer = new PDFRenderer(document);
		for (int i = 0; i < document.getNumberOfPages(); ++i) {
			images.add(renderer.renderImage(i, 4f));
		}
		BufferedImage[] imgArray = new BufferedImage[images.size()];
		for (int i = 0; i < imgArray.length; ++i) {
			imgArray[i] = images.get(i);
		}
		images = null;
		document.close();
		return imgArray;
	}
	
	public static BufferedImage[] getImagesFromFile(String fileName) throws IOException {
		return getImagesFromFile(new File(fileName));
	}

	public static BufferedImage[] getImagesFromFile(File file) throws IOException {
		images = new ArrayList<>();

		PDDocument document = PDDocument.load(file);

		PDFImageExtractor extractor = new PDFImageExtractor();
		for (int i = 0; i < document.getNumberOfPages(); ++i) {
			extractor.processPage(document.getPage(i));
		}
		BufferedImage[] imgArray = new BufferedImage[images.size()];
		for (int i = 0; i < imgArray.length; ++i) {
			imgArray[i] = images.get(i);
		}
		images = null;
		document.close();
		return imgArray;
	}

	private static class PDFImageExtractor extends PDFStreamEngine {

		@Override
		protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
			String operation = operator.getName();
			if ("Do".equals(operation)) {
				COSName objectName = (COSName) operands.get(0);
				PDXObject xobject = getResources().getXObject(objectName);
				if (xobject instanceof PDImageXObject) {
					PDImageXObject image = (PDImageXObject) xobject;
					int imageWidth = image.getWidth();
					int imageHeight = image.getHeight();

					BufferedImage bImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
					bImage = image.getImage();
					images.add(bImage);
					System.out.println("added another image.");
				}
			} else {
				super.processOperator(operator, operands);
			}
		}
	}
}
