package kr.co.syrup.adreport.web.event.define;

/**
 * 보관함 버튼 종류 정의
 */
public enum RepositoryButtonTypeDefine {
    CONFIRM("사용확인")
    ,URL("URL 접속")
    ,COPY("난수복사");

    final String repositoryButtonTypeStr;

    RepositoryButtonTypeDefine(String repositoryButtonTypeStr) {
        this.repositoryButtonTypeStr = repositoryButtonTypeStr;
    }
}
