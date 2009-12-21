<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="accounts"
             scope="request"
             type="java.util.Collection" />
<ul>
  <c:forEach var="account" items="${accounts}">
    <li><a href="/accounts/<c:out value="${account.id}" />/${account.domain}/">${account.domain} - ${account.username}</a></li>
  </c:forEach>
</ul>