<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html lang="en">

<head>
    <jsp:include page="../fragments/staticFiles.jsp" />
</head>

<body>
    <jsp:include page="../fragments/bodyHeader.jsp" />
    <div class="container xd-container">

        <h2>Owners</h2>

        <table id="owners" class="table table-striped table-hover">
            <thead>
                <tr>
                    <th style="width: 150px;">Name</th>
                    <th style="width: 200px;">Address</th>
                    <th>City</th>
                    <th>Telephone</th>
                    <th style="width: 100px;">Pets</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${listOwners}" var="owner">
                    <tr>
                        <td>
                            <spring:url value="/owners/${owner.id}" var="ownerUrl" htmlEscape="true" />
                            <a href="${ownerUrl}">
                                <c:out value="${owner.firstName} ${owner.lastName}" />
                            </a>
                        </td>
                        <td>
                            <c:out value="${owner.address}" />
                        </td>
                        <td>
                            <c:out value="${owner.city}" />
                        </td>
                        <td>
                            <c:out value="${owner.telephone}" />
                        </td>
                        <td>
                            <c:forEach var="pet" items="${owner.pets}">
                                <c:out value="${pet.name}" />
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>


        <div class="d-flex justify-content-between align-items-center mt-4">
            <div>
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination">
                            <c:if test="${currentPage > 1}">
                                <li class="page-item">
                                    <spring:url value="/owners" var="prevPageUrl">
                                        <spring:param name="page" value="${currentPage - 1}" />
                                        <spring:param name="lastName" value="${param.lastName}" />
                                    </spring:url>
                                    <a class="page-link" href="${prevPageUrl}">Previous</a>
                                </li>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <spring:url value="/owners" var="pageUrl">
                                    <spring:param name="page" value="${i}" />
                                    <spring:param name="lastName" value="${param.lastName}" />
                                </spring:url>
                                <li class="page-item <c:if test='${i == currentPage}'>active</c:if>'">
                                    <a class="page-link" href="${pageUrl}">${i}</a>
                                </li>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <li class="page-item">
                                    <spring:url value="/owners" var="nextPageUrl">
                                        <spring:param name="page" value="${currentPage + 1}" />
                                        <spring:param name="lastName" value="${param.lastName}" />
                                    </spring:url>
                                    <a class="page-link" href="${nextPageUrl}">Next</a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </c:if>
            </div>
            <div>
                <spring:url value="/owners/new" var="addOwnerUrl" htmlEscape="true" />
                <a class="btn btn-primary" href="${addOwnerUrl}">Add Owner</a>
            </div>
        </div>

        <jsp:include page="../fragments/footer.jsp" />
    </div>
</body>

</html>