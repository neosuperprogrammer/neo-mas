package com.tionsoft.tmg.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
/**
 * 파일 I/O 유틸리티
 * @author 솔루션개발1팀 이주용
 */
public class FileIo {
	public static byte[] fileToByte(String filepath) throws IOException {
		byte [] content = null;
		InputStream is = null;
		
		try {
			is = new FileInputStream(filepath);
			content = new byte[is.available() + 1];
			is.read(content);
		} catch(IOException e) {
			throw e;
		} finally {
			 try {
				 if(is != null) is.close();
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		}
		
		 return content;
	}

	public static byte[] urlToByte(String url, boolean getThumbnail) throws IOException {
		byte[] result = new byte[] {0x00};
		InputStream fin = null;
		
		try {
			
			URL url_ = new URL(url);
			fin = url_.openStream();
			
			ByteArrayOutputStream baos = null;
			
			if (getThumbnail) {
				ImageUtil imageUtil = new ImageUtil();
				baos = imageUtil.createThumbnail(fin, 200, 200);
				
			} else {
				baos = new ByteArrayOutputStream();
			
				while(true){
					int data = fin.read();  
					if(data == -1) break;
					baos.write(data);
				}
			}
			
			result = baos.toByteArray();
		} catch(IOException e) {
			throw e;
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch(Exception e) {
				}
			}
		}
		
		return result;
	}
	
	public static void urlDownload(String sourceUrl, String toPath) throws IOException {
		InputStream instream = null;
		FileOutputStream outstream = null;
		 byte[] buffer = new byte[4096];
		 
		try{
			File f = new File(toPath);
			File d = new File(f.getParent());
			if (!d.exists()) { 
				if (!d.mkdirs()) throw new IOException("Could not create directory : " + toPath);
			}

			URL url = new URL(sourceUrl);
			int read;
			instream = url.openStream();
			outstream = new FileOutputStream(f);
       		
			do {
				read = instream.read(buffer);
				if (read <= 0) break;
				outstream.write(buffer, 0, read);
			} while (true);				
			outstream.flush();

       		instream.close();
       		outstream.close();
       		
		} catch(IOException e) {
			throw e;
		} finally {
			try { if (instream != null) instream.close(); } catch (IOException e) { }
			try { if (outstream != null) outstream.close(); } catch (IOException e) { }
		}
		
	}
}
