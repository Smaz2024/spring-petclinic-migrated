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
            <c:if test="${owner['new']}">New </c:if> Owner
        </h2>

        <form:form modelAttribute="owner" class="form-horizontal" id="add-owner-form">
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">First Name</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="firstName" maxlength="30" pattern="[A-Za-z\s]{1,30}" title="First name must contain only letters and spaces (max 30)." required="true" />
                    <span class="help-inline">
                        <form:errors path="firstName" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Last Name</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="lastName" maxlength="30" pattern="[A-Za-z\s]{1,30}" title="Last name must contain only letters and spaces (max 30)." required="true" />
                    <span class="help-inline">
                        <form:errors path="lastName" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Address</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="address" maxlength="255" pattern=".{5,255}" title="Address must be at least 5 characters." required="true" />
                    <span class="help-inline">
                        <form:errors path="address" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">City</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="city" maxlength="80" />
                    <span class="help-inline">
                        <form:errors path="city" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-2 col-form-label">Telephone</label>
                <div class="col-sm-10">
                    <form:input class="form-control" path="telephone" maxlength="10" pattern="\d{10}" title="Telephone must be exactly 10 digits." required="true" />
                    <span class="help-inline">
                        <form:errors path="telephone" cssClass="text-danger" />
                    </span>
                </div>
            </div>
            <div class="mb-3 row">
                <div class="col-sm-offset-2 col-sm-10">
                    <c:choose>
                        <c:when test="${owner['new']}">
                            <button type="submit" class="btn btn-primary">Add Owner</button>
                        </c:when>
                        <c:otherwise>
                            <button type="submit" class="btn btn-primary">Update Owner</button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </form:form>

        <jsp:include page="../fragments/footer.jsp" />
    </div>
</body>

</html>