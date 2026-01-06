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

        <h2>
            <c:if test="${pet['new']}">New </c:if> Pet
        </h2>

        <form:form modelAttribute="pet" class="form-horizontal">
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Owner</label>
                <div class="col-sm-10">
                    <input type="text" readonly class="form-control-plaintext"
                        value="${pet.owner.firstName} ${pet.owner.lastName}">
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Name</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="name" size="30" maxlength="30" />
                    <span class="help-inline">
                        <form:errors path="name" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Birth Date</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="birthDate" placeholder="YYYY-MM-DD"
                        title="Enter a date in this format: YYYY-MM-DD" />
                    <span class="help-inline">
                        <form:errors path="birthDate" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Type</label>
                <div class="col-sm-10">
                    <form:select class="form-select" path="type" items="${types}" />
                    <span class="help-inline">
                        <form:errors path="type" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <div class="col-sm-offset-2 col-sm-10">
                    <c:choose>
                        <c:when test="${pet['new']}">
                            <button type="submit" class="btn btn-primary">Add Pet</button>
                        </c:when>
                        <c:otherwise>
                            <button type="submit" class="btn btn-primary">Update Pet</button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </form:form>

        <jsp:include page="../fragments/footer.jsp" />
    </div>
</body>

</html>