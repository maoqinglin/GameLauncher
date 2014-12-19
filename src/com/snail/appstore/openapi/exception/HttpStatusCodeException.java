package com.snail.appstore.openapi.exception;

import java.io.UnsupportedEncodingException;

import static com.snail.appstore.openapi.AppPlatFormConfig.DEFAULT_ENCODING;

/**
 * Http 状态码异常
 * 
 * @author zhangqf
 * @version 1.0 2011-5-16
 */
public class HttpStatusCodeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9208831416058066585L;

	private final Integer statusCode;

	private final String statusText;

	private final byte[] responseBody;

	/**
	 * Construct a new instance of {@code HttpStatusCodeException} based on a
	 * {@link HttpStatus} and status text.
	 * 
	 * @param statusCode
	 *            the status code
	 * @param statusText
	 *            the status text
	 */
	public HttpStatusCodeException(Integer statusCode, String statusText) {
		this(statusCode, statusText, null);
	}

	/**
	 * Construct a new instance of {@code HttpStatusCodeException} based on a
	 * {@link HttpStatus}, status text, and response body content.
	 * 
	 * @param statusCode
	 *            the status code
	 * @param statusText
	 *            the status text
	 * @param responseBody
	 *            the response body content, may be {@code null}
	 * @param responseCharset
	 *            the response body charset, may be {@code null}
	 * @since 3.0.5
	 */
	public HttpStatusCodeException(Integer statusCode, String statusText, byte[] responseBody) {
		super(statusCode + " " + statusText);
		this.statusCode = statusCode;
		this.statusText = statusText;
		this.responseBody = responseBody != null ? responseBody : new byte[0];

	}

	/**
	 * Returns the HTTP status code.
	 */
	public Integer getStatusCode() {
		return this.statusCode;
	}

	/**
	 * Returns the HTTP status text.
	 */
	public String getStatusText() {
		return this.statusText;
	}

	/**
	 * Returns the response body as a byte array.
	 * 
	 * @since 3.0.5
	 */
	public byte[] getResponseBodyAsByteArray() {
		return responseBody;
	}

	/**
	 * Returns the response body as a string.
	 * 
	 * @since 3.0.5
	 */
	public String getResponseBodyAsString() {
		try {
			return new String(responseBody, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException ex) {
			throw new InternalError(ex.getMessage());
		}
	}
}
