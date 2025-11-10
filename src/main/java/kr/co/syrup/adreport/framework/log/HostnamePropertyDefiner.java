package kr.co.syrup.adreport.framework.log;

import ch.qos.logback.core.PropertyDefinerBase;
import org.apache.commons.lang.StringUtils;

public class HostnamePropertyDefiner extends PropertyDefinerBase {
	@Override
	public String getPropertyValue() {
        return StringUtils.defaultString(System.getenv("HOSTNAME"), "default");
	}
}
