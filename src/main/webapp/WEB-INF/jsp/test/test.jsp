<%--
  User: ho
  Date: 2017. 12. 21.
  Time: 오전 11:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
    <script>
        $( function() {
            $( "#accordion" ).accordion({
                collapsible: true
                ,heightStyle: "content"
                // , icons: null
            });
        } );
    </script>
</head>
<body>
<fmt:setTimeZone value="London" scope="application"/>

<div >
test
</div>
</body>
</html>
