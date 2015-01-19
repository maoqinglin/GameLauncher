package com.ireadygo.app.gamelauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	  private static final String ALGORIGTHM_MD5 = "MD5";
	    private static final int CACHE_SIZE = 2048;
	    
	    /**
	     * 生成文件MD5值
	     * 在进行文件校验时，文件读取的缓冲大小[CACHE_SIZE]需与该方法的一致，否则校验失败
	     * 
	     * @param filePath
	     * @return
	     * @throws Exception
	     */
	    public static String generateFileMD5(String filePath) throws Exception {
	        String md5 = "";
	        File file = new File(filePath);
	        if (file.exists()) {
	            MessageDigest messageDigest = getMD5();
	            InputStream in = new FileInputStream(file);
	            byte[] cache = new byte[CACHE_SIZE];
	            int nRead = 0;
	            while ((nRead = in.read(cache)) != -1) {
	                messageDigest.update(cache, 0, nRead);
	            }
	            in.close();
	            byte data[] = messageDigest.digest();
	            md5 = byteArrayToHexString(data);
	         }
	        return md5;
	    }
	    
	    /**
	     * MD5摘要字节数组转换为16进制字符串
	     * 
	     * @param data MD5摘要
	     * @return
	     */
	    private static String byteArrayToHexString(byte[] data) {
	        // 用来将字节转换成 16 进制表示的字符
	        char hexDigits[] = {
	                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' 
	        };
	        // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
	        char arr[] = new char[16 * 2];
	        int k = 0; // 表示转换结果中对应的字符位置
	        // 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
	        for (int i = 0; i < 16; i++) {
	            byte b = data[i]; // 取第 i 个字节
	            // 取字节中高 4 位的数字转换, >>>为逻辑右移，将符号位一起右移
	            arr[k++] = hexDigits[b >>> 4 & 0xf];
	            // 取字节中低 4 位的数字转换
	            arr[k++] = hexDigits[b & 0xf];
	        }
	        // 换后的结果转换为字符串
	        return new String(arr);
	    }
	
	public static String getMD5(String content) {

		try {

			MessageDigest digest = getMD5();

			digest.update(content.getBytes());

			return getHashString(digest);

		} catch (Exception e) {

		}

		return null;

	}

	private static String getHashString(MessageDigest digest) {

		StringBuilder builder = new StringBuilder();

		for (byte b : digest.digest()) {

			builder.append(Integer.toHexString((b >> 4) & 0xf));

			builder.append(Integer.toHexString(b & 0xf));

		}

		return builder.toString().toUpperCase();

	}

	    /**
	     * 获取MD5实例
	     * 
	     * @return
	     * @throws NoSuchAlgorithmException 
	     */
	    private static MessageDigest getMD5() throws NoSuchAlgorithmException {
	        return MessageDigest.getInstance(ALGORIGTHM_MD5);
	    }
}
