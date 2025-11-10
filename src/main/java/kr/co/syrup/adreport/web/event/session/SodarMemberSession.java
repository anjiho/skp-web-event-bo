package kr.co.syrup.adreport.web.event.session;

import kr.co.syrup.adreport.web.event.dto.response.api.SodarMemberResDto;

public class SodarMemberSession {

    public static final String ATTR_NAME = "SODAR_MEMBER_SESSION";

    private static ThreadLocal<SodarMemberResDto> local = new ThreadLocal<>();

    public static void set(SodarMemberResDto credential){
        local.set(credential);
    }

    public static SodarMemberResDto get(){
        if (local.get() == null){
            SodarMemberResDto resDto = new SodarMemberResDto();
            resDto.setName("개발자");
            return resDto;
        } else {
            return local.get();
        }
    }

    public static String memberId() {
        return local.get().getMemberId();
    }

    public static String name() {
        return local.get().getName();
    }

}
