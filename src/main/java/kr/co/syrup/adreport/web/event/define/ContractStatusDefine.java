package kr.co.syrup.adreport.web.event.define;

/**
 * SODA 계약 상태값 정의
 */
public enum ContractStatusDefine {
    구매중("00"),
    승인대기("01"),
    반려("02"),
    승인중("0250"),
    서비스예정("03"),
    시버스진행("04"),
    일시중지("05"),
    서비스종료("06"),
    계약종료("07");

    public String contractStatusCode;

    ContractStatusDefine(String contractStatusCode) {
        this.contractStatusCode = contractStatusCode;
    }

    public String code() {
        return contractStatusCode;
    }
}
