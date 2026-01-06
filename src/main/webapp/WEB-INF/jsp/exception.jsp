<%@ page session="false" isErrorPage="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="fragments/staticFiles.jsp" />
</head>

<body>
    <jsp:include page="fragments/bodyHeader.jsp" />
    <div class="container xd-container">

        <div class="alert alert-danger" role="alert">
            <h4 class="alert-heading">Something happened...</h4>
            <p>${pageContext.exception}</p>
        </div>

        <div class="card">
            <div class="card-header">
                Exception Details
            </div>
            <div class="card-body">
                <!-- Exception stack trace -->
                <pre><c:forEach var="trace" items="${pageContext.exception.stackTrace}">
<c:out value="${trace}"/><br/>
</c:forEach></pre>
            </div>
        </div>

        <jsp:include page="fragments/footer.jsp" />
    </div>
</body>

</html>