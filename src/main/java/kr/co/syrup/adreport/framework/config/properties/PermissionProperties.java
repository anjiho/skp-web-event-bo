package kr.co.syrup.adreport.framework.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
//@ConfigurationProperties(prefix = "required.permission")
public class PermissionProperties {
    @Value("#{'${required.permission.permitList.ips:.}'.split(',')}")
    @Getter
    List<String> ipList;
}

