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

        <h2>Veterinarians</h2>

        <table id="vets" class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Specialties</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${vets.vetList}" var="vet">
                    <tr>
                        <td>
                            <c:out value="${vet.firstName} ${vet.lastName}" />
                        </td>
                        <td>
                            <c:forEach var="specialty" items="${vet.specialties}">
                                <span class="badge bg-secondary">
                                    <c:out value="${specialty.name}" />
                                </span>
                            </c:forEach>
                            <c:if test="${vet.nrOfSpecialties == 0}">
                                <span class="text-muted">none</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="d-flex gap-2">
            <a class="btn btn-outline-info" href="<spring:url value="/vets.xml" htmlEscape="true" />">View as XML</a>
            <a class="btn btn-outline-info" href="<spring:url value="/vets.json" htmlEscape="true" />">View as JSON</a>
            <a class="btn btn-outline-info" href="<spring:url value="/vets.pdf" htmlEscape="true" />">Download PDF</a>
        </div>

        <jsp:include page="../fragments/footer.jsp" />
    </div>
</body>

</html>