package com.bitfront.extruder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Extruder {

	
	private static void log(String string, Object ... args) {
		System.out.println(String.format(string, args));
	}
	
	private static void err(String string, Object ... args) {
		System.err.println(String.format(string, args));
	}
	
	private static void showUsage() {
		log("Usage: extruder [width_per_tile] [height_per_tile] src_file dst_file (optional)");
	}
	
	private static void extrude(int tileWidth, int tileHeight, String sourceFile, String destFile) throws IOException {
		log("extrude() %d %d %s %s", tileWidth, tileHeight, sourceFile, destFile);
		File f = new File(sourceFile);
		if(!f.exists()) {
			err("Error! %s does not exist", sourceFile);
			return;
		}
		BufferedImage image = ImageIO.read(f);
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();
		
		if((imageWidth % tileWidth) != 0) {
			err("Image with width %d is not evenly divided by tile width %d", imageWidth, tileWidth);
			return;
		}
		if((imageHeight % tileHeight) != 0) {
			err("Image with height %d is not evenly divided by tile height %d", imageHeight, tileHeight);
			return;
		}
		
		final int tileRows = imageHeight / tileHeight;
		final int tileColumns = imageWidth / tileWidth;
		
		BufferedImage destination = new BufferedImage(imageWidth + (tileColumns * 2), imageHeight + (tileRows * 2), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)destination.getGraphics();
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, destination.getWidth(), destination.getHeight());

		for(int x = 0; x < tileColumns; x++) {
			for(int y = 0; y < tileRows; y++) {
				int dx = 1 + (x * (tileWidth + 2));
				int dy = 1 + (y * (tileHeight + 2));
				int sx = x * tileWidth;
				int sy = y * tileHeight;
				// top-left pixel
				g.drawImage(image, dx - 1, dy - 1, dx, dy, sx, sy, sx + 1, sy + 1, null);
				// top-right pixel
				g.drawImage(image, dx + tileWidth, dy - 1, dx + tileWidth + 1, dy - 1 + 1, sx + tileWidth - 1, sy, sx + tileWidth - 1 + 1, sy + 1, null);
				// top edge
				g.drawImage(image, dx, dy - 1, dx + tileWidth, dy - 1 + 1, sx, sy, sx + tileWidth, sy + 1, null);

				// bottom-left pixel
				g.drawImage(image, dx - 1, dy + tileHeight, dx - 1 + 1, dy + tileHeight + 1, sx, sy + tileHeight - 1, sx + 1, sy + tileHeight - 1 + 1, null);
				// bottom-right pixel
				g.drawImage(image, dx + tileWidth, dy + tileHeight, dx + tileWidth + 1, dy + tileHeight + 1, sx + tileWidth - 1, sy + tileHeight - 1, sx + tileWidth - 1 + 1, sy + tileHeight - 1 + 1, null);
				// bottom edge
				g.drawImage(image, dx, dy + tileHeight, dx + tileWidth, dy + tileHeight + 1, sx, sy + tileHeight - 1, sx + tileWidth, sy + tileHeight - 1 + 1, null);

				// left edge
				g.drawImage(image, dx - 1, dy, dx - 1 + 1, dy + tileHeight, sx, sy, sx + 1, sy + tileHeight, null);
				// right edge
				g.drawImage(image, dx + tileWidth, dy, dx + tileWidth + 1, dy + tileHeight, sx + tileWidth - 1, sy, sx + tileWidth - 1 + 1, sy + tileHeight, null);
				
				// center
				g.drawImage(image, dx, dy, dx + tileWidth, dy + tileHeight, sx, sy, sx + tileWidth, sy + tileHeight, null);
			}
		}
		
		final String ext = destFile.substring(destFile.lastIndexOf('.') + 1);
		log("Writing extruded file %s", destFile);
		ImageIO.write(destination, ext, new File(destFile));
	}
	
	public static void main(String[] args) {		
		if(args.length < 3) {
			showUsage();
			return;
		}		
		
		try {
			int width = Integer.parseInt(args[0]);
			int height = Integer.parseInt(args[1]);
			String sourceFile = args[2];
			String destFile = (args.length == 4) ? args[3] : sourceFile;
			extrude(width, height, sourceFile, destFile);
		}
		catch(NumberFormatException e) {
			err("Unable to parse arguments: %s", e.getLocalizedMessage());
			showUsage();
		}
		catch (IOException e) {
			err("Unable to extrude borders: %s", e.getLocalizedMessage());
			showUsage();
		}
	}
}


