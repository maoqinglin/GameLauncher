package com.snail.appstore.openapi.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import static com.snail.appstore.openapi.AppPlatFormConfig.DEFAULT_ENCODING;

public class SecretCodeUtil {

	public final static String MD5 = "MD5";

	/**
	 * md5加密
	 * 
	 * @param plainText
	 * @return 加密后的大写字符串
	 */
	public static String getMD5ofStr(String plainText) {
		try {
			return getMD5ofStr(string2Bytes(plainText));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * md5加密
	 * 
	 * @param str
	 * @return 加密后的大写字符串
	 */
	public static String getMD5ofStr(byte str[]) {
		try {
			MessageDigest md = MessageDigest.getInstance(MD5);
			md.update(str);
			byte b[] = md.digest();
			return bytes2HexString(b);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 签名方法，用于生成签名。生成签名的描述见注3
	 * 
	 * @param params
	 *            传给服务器的参数
	 * @param secretKey
	 *            分配给您的APP_SECRET
	 */
	public static String sign(TreeMap<String, Object> params, String secretKey) {
		String result = null;
		if (params == null) return result;

		StringBuffer orgin = new StringBuffer();
		Set<Entry<String, Object>> set = params.entrySet();
		for (Entry<String, Object> e : set) {
			orgin.append(e.getKey()).append(e.getValue());
		}
		orgin.append(secretKey);
		return getMD5ofStr(orgin.toString());
	}

	/**
	 * 二行制转字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String bytes2HexString(byte[] bytes) {
		String hs = null;
		if (bytes != null) {
			final int size = bytes.length;
			if (size > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < size; i++) {
					String tmp = (java.lang.Integer.toHexString(bytes[i] & 0XFF));
					if (tmp.length() == 1) {
						sb.append("0" + tmp);
					} else {
						sb.append(tmp);
					}
				}
				hs = sb.toString().toUpperCase();
			}
		}
		return hs;
	}

	/**
	 * 把字符串转化成 Unicode Bytes.
	 * 
	 * @param s
	 *            String
	 * @return byte[]
	 */
	private static byte[] string2Bytes(String s) {
		byte[] bytes = null;
		if (s != null) {
			try {
				bytes = s.getBytes(DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

}
