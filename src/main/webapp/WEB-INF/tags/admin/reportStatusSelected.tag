<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="status" required="true" type="java.lang.String" %>
<%@ attribute name="statusTarget" required="true" type="java.lang.String" %>
<c:if test="${status eq statusTarget}">selected</c:if>