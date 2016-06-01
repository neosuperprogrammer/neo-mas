package com.tionsoft.tmg.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 파일 멀티 스레드 다운로드 
 * @author 서버개발실 이주용
 */
public class FileDownload {
	private ExecutorService pool;
	private Map<String, String> files;
	
	/**
	 * 다운로드 URL 파일명 설정
	 * @param files
	 */
	public void setFiles(Map<String, String> files) {
		this.pool	= Executors.newFixedThreadPool(files.size());
		this.files	 = files;
	}
	
	/**
	 * 파일 다운로드 실행
	 * @throws InterruptedException
	 */
	public void execute() throws InterruptedException {
		for (String url : files.keySet()) {
		    pool.submit(new DownloadTask(url, files.get(url)));
		}
		pool.shutdown();
		pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);		
	}
	
	/**
	 * 
	 * @author 서버개발실 이주용
	 */
	private static class DownloadTask implements Runnable {
	    private String sourceUrl;
	    private final String toPath;

	    public DownloadTask(String sourceUrl, String toPath) {
	        this.sourceUrl = sourceUrl;
	        this.toPath = toPath;
	    }

	    /**
	     * 파일 다운로드 실행
	     */
	    @Override
	    public void run() {
			InputStream instream = null;
			FileOutputStream outstream = null;
			byte[] buffer = new byte[4096];

			try{
				File f = new File(toPath);
				File d = new File(f.getParent());
				if (!d.exists()) {
					if (!d.mkdirs()) throw new IOException();
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
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} finally {
				try {
					if (instream != null) instream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					if (outstream != null) outstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
}
