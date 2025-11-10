package kr.co.syrup.adreport.web.event.session;

import kr.co.syrup.adreport.web.event.dto.response.CacheableInfoResDto;

public class CacheableSession {

    public static final String ATTR_NAME = "SODAR_VERSION_ATTR";

    private static ThreadLocal<Long> local = new ThreadLocal<>();

    public static void set(Long sodarVersion){
        local.set(sodarVersion);
    }

    public static Long get(){
        return local.get();
    }
}
