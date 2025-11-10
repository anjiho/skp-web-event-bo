var noticeList =function () {
    var params = {};

    $.ajax({
        headers:{
            "X-Auth-Token":$.cookie("xat")
        },
        url: "/admin/mng/notices",
        type: "POST",
        data: $.param(params),
        dataType:"html"
    }).done( function( res ){
        // 성공 했을때, 지금은 200OK면 일단 호출
        $("#pageContent").html(res);
        noticeListPageInit();
    }).fail( function( res ){
        errorProcess(res);
    });
};
var isModify = false;
var noticeListPageInit = function () {
    $(".noticeModifyCls").off("click").on("click", function () {
        var $this = $(this);
        var $tr = $this.closest("tr");
        if(!isModify){
            $this.text("완료");
            $tr.find(".editableCls").prop("disabled",false);
            isModify = true;
        }else {
            saveNotice($tr.data("noticeNo"),$tr.find("input").val(),$tr.find("textarea").val());
            $this.text("수정");
            $tr.find(".editableCls").attr("disabled", true);
            isModify = false;
        }
    });
    $(".noticeUseYnCls").off("click").on("click", function () {

    });
    $(".noticeDeleteCls").off("click").on("click", function () {

    });
    $("#noticeSaveBtn").off("click").on("click", function () {
        saveNotice(null,$("#noticeTitle").val(),$("#noticeContents").val());
    });
    $("#viewNoticeSaveBtn").off("click").on("click", function () {
        $("#registerNoticeArea").show();
    });
};

var saveNotice = function (noticeNo, title, contents) {
    var params ={};
    if(noticeNo){
        params.noticeNo = noticeNo;
    }
    params.title = title;
    params.contents = contents;
    if(!params.title){
        alert("제목을 입력해주세요.");
        return;
    }
    if(!params.contents){
        alert("내용을 입력해주세요.");
        return;
    }
    console.log("save notice params = ",params);

    $.ajax({
        headers:{
            "X-Auth-Token":$.cookie("xat")
        },
        url: "/admin/mng/notice/save",
        type: "POST",
        data: $.param(params),
        dataType:"json"
    }).done( function( res ){
        // 성공 했을때, 지금은 200OK면 일단 호출
        alert("완료되었습니다.");
        //$("#registerNoticeArea").hide();
        noticeList();
    }).fail( function( res ){
        errorProcess(res);
    });
};