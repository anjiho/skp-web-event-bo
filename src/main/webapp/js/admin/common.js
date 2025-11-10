var errorProcess = function (res) {
    console.log(res);
    if(res.status == 403){
        window.location.href = "/admin/login";
    }
    if(res.responseJSON && res.responseJSON.resultMessage)
        alert( res.responseJSON.resultMessage );
    else if(res.responseText)
        alert( res.responseText );
    else {
        alert("error\n"+JSON.stringify(res));
    }
};