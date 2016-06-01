package com.tionsoft.tmg.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageUtil extends Thread {
	
	private List<Map<String, String>> thumbnailImages = null;

	public void setThumbnailImageList(List<Map<String, String>> thumbnailImages) {
		this.thumbnailImages = thumbnailImages;
	}
	
	public void run() {
		String url = "";
		String downDir = "";
		String downPath = "";
		String fileName = "";
		String thumbnailFileName = "";
		String thumbnailPath = "";
		
		try {
			for (Map<String, String> item : thumbnailImages) {
				url = item.get("DOWNLOAD_URL");
				downDir = item.get("DOWNLOAD_DIR");
				fileName = item.get("DOWNLOAD_FILE_NAME");
				thumbnailFileName = item.get("THUMBNAIL_FILE_NAME");
				
				downPath = downDir + "/" + fileName;
				thumbnailPath = downDir + "/" + thumbnailFileName;
				
				downloadImage(url, downPath);
				createThumbnail(downPath, thumbnailPath, 200, 200);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void downloadImage(String url, String path) throws Exception {
		File file = new File(path);
		if (file.exists() || url.isEmpty() || path.equals("/"))
			return;
		
		InputStream in = null;
		OutputStream out = null;
		byte[] buffer = new byte[4096];
		
		try {
			in = new URL(url).openStream();
			out = new BufferedOutputStream(new FileOutputStream(path));

			do {
				int read = in.read(buffer);
				if (read <= 0) break;
				out.write(buffer, 0, read);
			} while (true);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try { if (in != null) in.close(); } catch (Exception e) {}
			try { if (out != null) out.close(); } catch (Exception e) {}
		}
	}
	
	public void createThumbnail(String srcPath, String destPath, int width, int height) throws IOException{
		File file = new File(destPath);
		if (file.exists() || srcPath.equals("/") || destPath.equals("/"))
			return;
		
		File save = new File(destPath);
		FileInputStream fis = new FileInputStream(srcPath);
		BufferedImage im = ImageIO.read(fis);
		
		BufferedImage thumb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = thumb.createGraphics();
		
		g2.drawImage(im, 0, 0, width, height, null);
		ImageIO.write(thumb, "jpg", save);
	}	
	
	
	public ByteArrayOutputStream createThumbnail(InputStream fis, int width, int height) throws IOException{
		
		System.out.println("ImageUtil.createThumbnail.begin");
		
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		BufferedImage im = ImageIO.read(fis);
		
		BufferedImage thumb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = thumb.createGraphics();
		
		g2.drawImage(im, 0, 0, width, height, null);
		ImageIO.write(thumb, "jpg", ret);
		
		System.out.println("ImageUtil.createThumbnail.end");
		
		return ret;
	}	


}
