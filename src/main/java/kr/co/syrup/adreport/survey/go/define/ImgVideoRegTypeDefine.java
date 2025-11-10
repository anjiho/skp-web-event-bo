package kr.co.syrup.adreport.survey.go.define;

/*
 *  보기 유형 정의
 */
public enum ImgVideoRegTypeDefine {
    N("등록안함"), IMG("이미지"), VIDEO("영상");

    final String imgVideoRegTypeStr;

    ImgVideoRegTypeDefine(String imgVideoRegTypeStr) {
        this.imgVideoRegTypeStr = imgVideoRegTypeStr;
    }
}
