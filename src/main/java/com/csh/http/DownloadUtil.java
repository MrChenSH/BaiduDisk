package com.csh.http;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.*;
import com.csh.utils.CookieUtil;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by amosli on 14-7-2.
 */

public class DownloadUtil {

	private static final Logger logger = Logger.getLogger(DownloadUtil.class);


	// 定义下载资源的路径
	private String url;
	// 指定所下载的文件的保存位置
	private String targetFile;
	// 定义需要使用多少线程下载资源
	private int threadCount;
	// 定义下载的线程对象
	private DownThread[] threads;
	// 定义下载的文件的总大小
	private int fileSize;

	private long total;

	public int getFileSize() {
		return fileSize;
	}

	public DownloadUtil(String url, String targetFile, int threadNum) {
		this.url = url;
		this.threadCount = threadNum;
		// 初始化threads数组
		threads = new DownThread[threadNum];
		this.targetFile = targetFile;
	}

	public void download() {
		try {
			HttpConnection conn = HttpConnection.create(url, Method.GET).setCookie(CollectionUtil.join(CookieUtil.COOKIES, "; "));
			if (conn.responseCode() == 302) {
				this.url = conn.header(Header.LOCATION);
				conn = HttpConnection.create(url, Method.GET).setCookie(CollectionUtil.join(CookieUtil.COOKIES, "; "));
			}

			if (conn.responseCode() == 200) {
				fileSize = conn.getHttpURLConnection().getContentLength();

				int blockSize = fileSize / threadCount;
				RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
				// 设置本地文件的大小
				file.setLength(fileSize);
				file.close();
				for (int i = 0; i < threadCount; i++) {
					int startThread = i * blockSize;
					int endThread = (i + 1) * blockSize - 1;
					if (i == threadCount - 1) endThread = fileSize - 1;
					new DownloadThread(i, startThread, endThread).start();

					// 计算每条线程的下载的开始位置
//					int startPos = i * blockSize;
//					// 每个线程使用一个RandomAccessFile进行下载
//					RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
//					// 定位该线程的下载位置
//					currentPart.seek(startPos);
//					// 创建下载线程
//					threads[i] = new DownThread(startPos, blockSize, currentPart);
//					// 启动下载线程
//					threads[i].start();
				}
			} else throw new RuntimeException(conn.responseCode() + "");
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}

	/**
	 * 获取下载的完成百分比
	 */
	public double getCompleteRate() {
		return Double.valueOf(getTotal()) / fileSize;
	}

	/**
	 * 统计多条线程已经下载的总大小
	 *
	 * @return
	 */
	public long getCompleteSize() {
		long sumSize = 0;
		for (int i = 0; i < threadCount; i++) {
			sumSize += threads[i].length;
		}
		System.out.println(sumSize);
		return sumSize;
	}

	public long getTotal() {
		return total;
	}

	private class DownThread extends Thread {
		// 当前线程的下载位置
		private int startPos;
		// 定义当前线程负责下载的文件大小
		private int currentPartSize;
		// 当前线程需要下载的文件块
		private RandomAccessFile currentPart;
		// 定义已经该线程已下载的字节数
		public int length;

		public DownThread(int startPos, int currentPartSize, RandomAccessFile currentPart) {
			this.startPos = startPos;
			this.currentPartSize = currentPartSize;
			this.currentPart = currentPart;
		}

		@Override
		public void run() {
			try {
				HttpConnection conn = new HttpConnection(url, Method.GET)
						.header(Header.CONNECTION, "keep-alive", true)
						.setCookie(CollectionUtil.join(CookieUtil.COOKIES, "; "));

				InputStream stream = conn.getInputStream();

				// 跳过startPos个字节，表明该线程只下载自己负责哪部分文件。
				stream.skip(this.startPos);
				byte[] buffer = new byte[1024];
				int hasRead = 0;
				// 读取网络数据，并写入本地文件
				while (length < currentPartSize
						&& (hasRead = stream.read(buffer)) != -1) {
					currentPart.write(buffer, 0, hasRead);
					// 累计该线程下载的总大小
					length += hasRead;
				}
				currentPart.close();
				stream.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}


	public class DownloadThread extends Thread {
		private int threadId;
		private int endThread;
		private int startThred;

		public DownloadThread(int threadId, int startThred, int endThread) {
			this.threadId = threadId;
			this.startThred = startThred;
			this.endThread = endThread;
		}

		public void run() {
			//分段请求网络连接，分段保存在本地
			try {
				logger.info("理论线程:" + threadId + ",开始位置:" + startThred + ",结束位置:" + endThread);

				HttpConnection conn = HttpConnection.create(url, Method.GET);
//						.header(Header.CONNECTION, "keep-alive", true)
//						设置分段下载的头信息  Range:做分段
//						.header("range", "bytes=" + startThred + "-" + endThread, true);
//						.setCookie(CollectionUtil.join(CookieUtil.COOKIES, "; "));

				/*if(file.exists()) {    //是否断点
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					String lastPostion_str = bufferedReader.readLine();
					startThred = Integer.parseInt(lastPostion_str);
					bufferedReader.close();
				}*/

				int code = conn.responseCode();

				if (code == 200 || code == 206) {    //200:请求全部资源成功  206:代表部分资源请求成功
					InputStream inputStream = conn.getInputStream();
					RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
					randomAccessFile.seek(startThred);
					byte[] buffer = new byte[1024 * 10];
					int length = -1;
//					int total = 0;//记录下载的总量
					logger.info("实际线程:" + threadId + ",开始位置:" + startThred + ",结束位置:" + endThread);
					while ((length = inputStream.read(buffer)) != -1) {
						randomAccessFile.write(buffer, 0, length);
						synchronized (DownloadThread.class) {
							total += length;
						}

//						int currentThreadPostion = startThred + total;

					}
					randomAccessFile.close();
					inputStream.close();
					logger.info("线程:" + threadId + "下载完毕");
				} else throw new RuntimeException(conn.responseCode() + "");

			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}
	}
}