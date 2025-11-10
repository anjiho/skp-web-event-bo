package kr.co.syrup.adreport.framework.converter;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 리턴 타입이 void일 때 처리하는 모듈
 *
 */
public class VoidHttpMessageConverter extends StringHttpMessageConverter {

	private final Charset defaultCharset;

	/**
	 * A default constructor that uses {@code "ISO-8859-1"} as the default charset.
	 */
	public VoidHttpMessageConverter() {
		this(DEFAULT_CHARSET);
	}

	/**
	 * A constructor accepting a default charset to use if the requested content
	 * type does not specify one.
	 */
	public VoidHttpMessageConverter(Charset defaultCharset) {
		super(defaultCharset);
		this.defaultCharset = defaultCharset;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return void.class.equals(clazz);
	}

	@Override
	protected void writeInternal(String s, HttpOutputMessage outputMessage) throws IOException {
		Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
		StreamUtils.copy("", charset, outputMessage.getBody());
	}

	private Charset getContentTypeCharset(MediaType contentType) {
		if (contentType != null && contentType.getCharset() != null) {
			return contentType.getCharset();
		}
		else {
			return this.defaultCharset;
		}
	}
}
