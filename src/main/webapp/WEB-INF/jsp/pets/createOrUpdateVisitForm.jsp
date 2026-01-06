<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="../fragments/staticFiles.jsp" />
</head>

<body>
    <jsp:include page="../fragments/bodyHeader.jsp" />
    <div class="container xd-container">

        <h2>New Visit</h2>

        <h5 class="mb-3">Pet</h5>
        <div class="table-responsive mb-4">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Birth Date</th>
                        <th>Type</th>
                        <th>Owner</th>
                    </tr>
                </thead>
                <tr>
                    <td>
                        <c:out value="${pet.name}" />
                    </td>
                    <td>
                        <c:out value="${pet.birthDate}" />
                    </td>
                    <td>
                        <c:out value="${pet.type.name}" />
                    </td>
                    <td>
                        <c:out value="${pet.owner.firstName} ${pet.owner.lastName}" />
                    </td>
                </tr>
            </table>
        </div>

        <form:form modelAttribute="visit" class="form-horizontal">
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Date</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="date" placeholder="YYYY-MM-DD"
                        title="Enter a date in this format: YYYY-MM-DD" />
                    <span class="help-inline">
                        <form:errors path="date" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Description</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="description" maxlength="255" />
                    <span class="help-inline">
                        <form:errors path="description" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-primary">Add Visit</button>
                </div>
            </div>
        </form:form>

        <br />
        <h5 class="mb-3">Previous Visits</h5>
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <tr>
                    <th>Date</th>
                    <th>Description</th>
                </tr>
                <c:forEach var="visit" items="${pet.visits}">
                    <c:if test="${!visit['new']}">
                        <tr>
                            <td>
                                <c:out value="${visit.date}" />
                            </td>
                            <td>
                                <c:out value="${visit.description}" />
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>
        </div>

        <jsp:include page="../fragments/footer.jsp" />
    </div>
</body>

</html>