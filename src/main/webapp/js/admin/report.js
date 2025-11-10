var reportList =function () {
    var params = {};
    if ($("#status").val())
        params.status = $("#status").val();
    if ($("#nickname").val())
        params.nickname = $("#nickname").val();
    if ($("#reportNickname").val())
        params.reportNickname = $("#reportNickname").val();

    $.ajax({
        headers:{
           "X-Auth-Token":$.cookie("xat")
        },
        url: "/admin/mng/reports",
        type: "POST",
        data: $.param(params),
        dataType:"html"
    }).done( function( res ){
        // 성공 했을때, 지금은 200OK면 일단 호출
        $("#pageContent").html(res);
        reportListPageInit();
    }).fail( function( res ){
        errorProcess(res);
    });
};

var reportListPageInit = function () {
    $(".searchAreaCls").hide();
    $("#reportSearchArea").show();
    $( "#status" ).selectmenu();
    $("#reportSearchBtn").off("click").click(function () {
        reportList();
    });
    $(".reportNoTdCls").off("click").click(function () {
        var $this = $(this);
        // window.location = $this.attr('href');
        reportDetail($this.data("reportNo"));
    });
};

var reportDetail = function (reportNo) {
    $.ajax({
        headers:{
            "X-Auth-Token":$.cookie("xat")
        },
        url: "/admin/mng/report/"+reportNo,
        type: "POST",
        data: {},
        dataType:"html"
    }).done( function( res ){
        // 성공 했을때, 지금은 200OK면 일단 호출
        $(".searchAreaCls").hide();
        $("#pageContent").html(res);
        reportDetailPageInit();
    }).fail( function( res ){
        errorProcess(res);
    });

};

var reportDetailPageInit = function () {
    $("#reportProcessBtn").off("click").click(function () {
        reportProcess();
    });
    $("#reportRejectBtn").off("click").click(function () {
        reportReject();
    });
    $("#reportCancelBtn").off("click").click(function () {
        reportCancel();
    });
};

var reportProcess = function () {
    var reason = $("#processContents").val();
    if(!reason){
        alert("내용을 입력하세요.");
        return;
    }
    reportCall("/admin/mng/report/processToSuspension", reason);
};

var reportReject = function () {
    var reason = $("#processContents").val();
    if(!reason){
        alert("내용을 입력하세요.");
        return;
    }
    reportCall("/admin/mng/report/reject", reason);
};

var reportCancel = function () {
    var reason = $("#suspensionCnclDesc").val();
    if(!reason){
        alert("내용을 입력하세요.");
        return;
    }
    reportCall("/admin/mng/report/processToNormal", reason);
};

var reportCall = function (url, reason) {
    var params = {};
    params.reportNo = $("#reportNoTd").data("reportNo");
    params.targetUserNo = $("#targetUserNoTd").data("userNo");
    params.reason = reason;
    $.ajax({
        headers:{
            "X-Auth-Token":$.cookie("xat")
        },
        url: url,
        type: "POST",
        data: $.param(params),
        dataType:"json"
    }).done( function( res ){
        // 성공 했을때, 지금은 200OK면 일단 호출
        console.log("res", res);
        alert("완료했습니다.");
        reportDetail(res.data.result);
    }).fail( function( res ){

        if(res.responseJSON && res.responseJSON.resultMessage)
            alert( res.responseJSON.resultMessage );
        else if(res.responseText)
            alert( res.responseText );
        else alert("error");
    });
};