package com.snail.appstore.openapi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.snail.appstore.openapi.exception.HttpStatusCodeException;

import static com.snail.appstore.openapi.AppPlatFormConfig.DEFAULT_ENCODING;

/**
 * Http 连接处理工具
 * 
 * @author zhangqf
 * @version 1.0 2011-5-16
 */
public final class HttpUtil {
	private static final int CONNECT_TIMEOUT = 5000;// （单位：毫秒)
	private static final int BUFFER_SIZE = 4096;

	/**
	 * HTTP GET 请求
	 * 
	 * @param reqUrl
	 *            请求URL
	 * @return
	 * @throws HttpStatusCodeException
	 *             , Exception 返回正文信息
	 */
	public static String doGet(String restUrl) throws HttpStatusCodeException, Exception {
		HttpURLConnection urlConn = null;
		try {
			URL url = new URL(restUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
			urlConn.setRequestProperty("User-Agent", "D1xn/Client");
			urlConn.setConnectTimeout(CONNECT_TIMEOUT);

			return getBody(urlConn);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
				urlConn = null;
			}
		}

	}

	/**
	 * Http POST 请求
	 * 
	 * @param restUrl
	 *            请求URL
	 * @param parameters
	 *            请求参数
	 * @return
	 * @throws HttpStatusCodeException
	 *             , Exception 返回正文信息
	 */
	public static String doPost(String restUrl, Map<String, String> parameters) throws HttpStatusCodeException, Exception {
		HttpURLConnection urlConn = null;
		try {
			urlConn = sendPost(restUrl, parameters);
			return getBody(urlConn);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
				urlConn = null;
			}
		}
	}

	/**
	 * Http PUT 请求
	 * 
	 * @param restUrl
	 *            请求URL
	 * @param parameters
	 *            请求参数
	 * @return
	 * @throws HttpStatusCodeException
	 *             , Exception 返回正文信息
	 */
	public static String doPut(String restUrl, Map<String, String> parameters) throws HttpStatusCodeException, Exception {
		parameters.put("_method", "PUT");
		return doPost(restUrl, parameters);
	}

	/**
	 * Http DELETE 请求
	 * 
	 * @param restUrl
	 *            请求URL
	 * @param parameters
	 *            请求参数
	 * @return
	 * @throws HttpStatusCodeException
	 *             , Exception 返回正文信息
	 */
	public static String doDelete(String restUrl) throws HttpStatusCodeException, Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("_method", "DELETE");
		return doPost(restUrl, parameters);
	}

	/**
	 * 上传文件
	 * 
	 * @param uploadUrl
	 *            上传URL
	 * @param parameters
	 *            参数
	 * @param fileParamName
	 *            文件参数名
	 * @param filename
	 *            文件名称
	 * @param contentType
	 *            类型
	 * @param data
	 *            文件内容
	 * @return
	 * @throws HttpStatusCodeException
	 *             , Exception 返回正文信息
	 */
	public static String doUploadFile(String uploadUrl, Map<String, String> parameters, String fileParamName, String filename,
		String contentType, byte[] data) throws HttpStatusCodeException, Exception {
		HttpURLConnection urlConn = null;
		try {
			urlConn = sendFormdata(uploadUrl, parameters, fileParamName, filename, contentType, data);
			return getBody(urlConn);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	/**
	 * POST数据表单
	 * 
	 * @param restUrl
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private static HttpURLConnection sendPost(String restUrl, Map<String, String> parameters) throws Exception {
		HttpURLConnection urlConn = null;
		try {
			String params = generatorParamString(parameters);
			URL url = new URL(restUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("User-Agent", "D1xn/Client");
			urlConn.setConnectTimeout(CONNECT_TIMEOUT);
			urlConn.setDoOutput(true);
			byte[] b = params.getBytes();
			urlConn.getOutputStream().write(b, 0, b.length);
			urlConn.getOutputStream().flush();

		} finally {
			if (urlConn != null) {
				urlConn.getOutputStream().close();
			}
		}
		return urlConn;
	}

	/**
	 * 上传文件表单
	 * 
	 * @param uploadUrl
	 * @param parameters
	 * @param fileParamName
	 * @param filename
	 * @param contentType
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static HttpURLConnection sendFormdata(String uploadUrl, Map<String, String> parameters, String fileParamName,
		String filename, String contentType, byte[] data) throws Exception {
		HttpURLConnection urlConn = null;
		OutputStream os = null;
		try {
			URL url = new URL(uploadUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(CONNECT_TIMEOUT);
			urlConn.setDoOutput(true);

			urlConn.setRequestProperty("connection", "keep-alive");

			String boundary = "-----------------------------114975832116442893661388290519"; // 分隔符
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			boundary = "--" + boundary;
			StringBuffer params = new StringBuffer();
			if (parameters != null) {
				for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = parameters.get(name);
					params.append(boundary + "\r\n");
					params.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
					params.append(URLEncoder.encode(value, DEFAULT_ENCODING));
					// params.append(value);
					params.append("\r\n");
				}
			}

			StringBuilder sb = new StringBuilder();
			// sb.append("\r\n");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + filename + "\"\r\n");
			sb.append("Content-Type: " + contentType + "\r\n\r\n");
			byte[] fileDiv = sb.toString().getBytes();
			byte[] endData = ("\r\n" + boundary + "--\r\n").getBytes();
			byte[] ps = params.toString().getBytes();

			os = urlConn.getOutputStream();
			os.write(ps);
			os.write(fileDiv);
			os.write(data);
			os.write(endData);

			os.flush();

		} finally {
			if (os != null) {
				os.close();
			}
		}
		return urlConn;
	}

	/**
	 * Return the body of the message as an input stream.
	 * 
	 * @return the input stream body
	 * @throws IOException
	 *             in case of I/O Errors
	 */
	private static String getBody(HttpURLConnection urlConn) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		if (urlConn.getResponseCode() != 200) {
			copy(urlConn.getErrorStream(), out);
			throw new HttpStatusCodeException(urlConn.getResponseCode(), urlConn.getResponseMessage(), out.toByteArray());
		} else {
			copy(urlConn.getInputStream(), out);
			return new String(out.toByteArray(), DEFAULT_ENCODING);
		}
	}

	/**
	 * 将parameters中数据转换成用"&"链接的http请求参数形式
	 * 
	 * @param parameters
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String generatorParamString(Map<String, String> parameters) throws UnsupportedEncodingException {
		StringBuffer params = new StringBuffer();
		if (parameters != null) {
			for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
				String name = iter.next();
				String value = parameters.get(name);
				params.append(name).append("=");
				if (value != null) {
					params.append(URLEncoder.encode(value, DEFAULT_ENCODING));
				} else {
					params.append(value);
				}

				if (iter.hasNext()) {
					params.append("&");
				}
			}
		}
		return params.toString();
	}

	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Closes both streams when done.
	 * 
	 * @param in
	 *            the stream to copy from
	 * @param out
	 *            the stream to copy to
	 * @return the number of bytes copied
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException {
		try {
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}
}
