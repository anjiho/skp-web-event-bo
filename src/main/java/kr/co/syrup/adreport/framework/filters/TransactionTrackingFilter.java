package kr.co.syrup.adreport.framework.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionTrackingFilter extends OncePerRequestFilter {

	private static final String TRANSACTION_ID_RANDOM_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static final String TRANSACTION_ID_HEADER = "Transaction-Id";

	private static final String TRANSACTION_ID = "context.transactionId";
	public TransactionTrackingFilter() {
	}

	@Override
	protected void initFilterBean() throws ServletException {
	}

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		final String transactionId = getTransactionId(request, response);
		TransactionContextHolder.setTransactionId(transactionId);
		MDC.put(TRANSACTION_ID, transactionId);
		try {
			String oldTransactionId = response.getHeader(TRANSACTION_ID_HEADER);
			if (!StringUtils.hasLength(oldTransactionId)) {
				response.addHeader(TRANSACTION_ID_HEADER, transactionId);
			}
			chain.doFilter(request, response);
		} finally {
			TransactionContextHolder.clearTransactionId();
			MDC.remove(TRANSACTION_ID);
		}
	}

	protected String getTransactionId(HttpServletRequest request, HttpServletResponse response) {
		final String uri = request.getRequestURI();
		String transactionIdfromResponse  = response.getHeader(TRANSACTION_ID_HEADER);
	    if (StringUtils.hasText(transactionIdfromResponse)) {
	        log.debug("Start transaction! ( Forward transaction id : {} ) from URI \"{}\"", transactionIdfromResponse, uri);
	        return transactionIdfromResponse;
	    }
	    String transactionIdfromRequest = request.getHeader(TRANSACTION_ID_HEADER);
        if (StringUtils.hasText(transactionIdfromRequest)) {
            log.debug("Start transaction! ( Receive transaction id : {} ) from URI \"{}\"", transactionIdfromRequest, uri);
		    return transactionIdfromRequest;
		}
	    String generatedTransactionId = generateTransactionId();
	    log.debug("Start transaction! ( Generate transaction id : {} ) from URI \"{}\"", generatedTransactionId, uri);
        return generatedTransactionId;
    }

	/**
	 * 트랜잭션 번호를 생성한다. 타임스템프 + 랜덤 숫자 9자리
	 *
	 * @return
	 */
	protected String generateTransactionId() {
		return RandomStringUtils.random(5, TRANSACTION_ID_RANDOM_CHAR) +
		        "-" + RandomStringUtils.random(7, TRANSACTION_ID_RANDOM_CHAR);
	}
}
