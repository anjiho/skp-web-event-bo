<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="status" required="true" type="java.lang.String" %>
<c:if test="${status eq 'REQ'}">신고</c:if>
<c:if test="${status eq 'RJT'}">반려</c:if>
<c:if test="${status eq 'END'}">완료</c:if>