<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html lang="en">

<head>
    <jsp:include page="../fragments/staticFiles.jsp" />
</head>

<body>

    <jsp:include page="../fragments/bodyHeader.jsp" />

    <div class="container xd-container">
        <h2>Find Owners</h2>

        <spring:url value="/owners" var="formUrl" />
        <form:form modelAttribute="owner" action="${formUrl}" method="get" class="form-horizontal"
            id="search-owner-form">
            <div class="row mb-3">
                <label class="col-sm-2 col-form-label">Last name</label>
                <div class="col-sm-10">
                    <div class="input-group">
                        <form:input class="form-control" path="lastName" size="30" maxlength="80" />
                        <span class="help-inline">
                            <form:errors path="*" cssClass="text-danger" />
                        </span>
                    </div>
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-primary">Find Owner</button>
                </div>
            </div>
        </form:form>

        <br />
        <spring:url value="/owners/new" var="addOwnerUrl" htmlEscape="true"/>
        <a class="btn btn-secondary" href='${addOwnerUrl}'>Add Owner</a>

        <jsp:include page="../fragments/footer.jsp" />
    </div>

</body>

</html>