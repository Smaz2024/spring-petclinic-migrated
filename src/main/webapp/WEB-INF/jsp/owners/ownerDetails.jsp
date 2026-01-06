<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="../fragments/staticFiles.jsp" />
</head>

<body>
    <jsp:include page="../fragments/bodyHeader.jsp" />
    <div class="container xd-container">

        <h2>Owner Information</h2>

        <table class="table table-striped table-hover" style="max-width: 600px;">
            <tr>
                <th>Name</th>
                <td><b>
                        <c:out value="${owner.firstName} ${owner.lastName}" />
                    </b></td>
            </tr>
            <tr>
                <th>Address</th>
                <td>
                    <c:out value="${owner.address}" />
                </td>
            </tr>
            <tr>
                <th>City</th>
                <td>
                    <c:out value="${owner.city}" />
                </td>
            </tr>
            <tr>
                <th>Telephone</th>
                <td>
                    <c:out value="${owner.telephone}" />
                </td>
            </tr>
        </table>

        <spring:url value="{ownerId}/edit" var="editUrl">
            <spring:param name="ownerId" value="${owner.id}" />
        </spring:url>
        <a href="${editUrl}" class="btn btn-primary">Edit Owner</a>

        <spring:url value="{ownerId}/pets/new" var="addPetUrl">
            <spring:param name="ownerId" value="${owner.id}" />
        </spring:url>
        <a href="${addPetUrl}" class="btn btn-success">Add New Pet</a>

        <br />
        <br />
        <br />

        <h2>Pets and Visits</h2>

        <table class="table table-striped table-hover">
            <c:forEach var="pet" items="${owner.pets}">
                <tr>
                    <td valign="top" style="width: 150px;">
                        <dl class="row">
                            <dt class="col-sm-4">Name</dt>
                            <dd class="col-sm-8">
                                <c:out value="${pet.name}" />
                            </dd>

                            <dt class="col-sm-4">Birth Date</dt>
                            <dd class="col-sm-8">
                                <c:out value="${pet.birthDate}" />
                            </dd>

                            <dt class="col-sm-4">Type</dt>
                            <dd class="col-sm-8">
                                <c:out value="${pet.type.name}" />
                            </dd>
                        </dl>
                    </td>
                    <td valign="top">
                        <table class="table table-condensed">
                            <thead>
                                <tr>
                                    <th>Visit Date</th>
                                    <th>Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="visit" items="${pet.visits}">
                                    <tr>
                                        <td>
                                            <c:out value="${visit.date}" />
                                        </td>
                                        <td>
                                            <c:out value="${visit.description}" />
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td>
                                        <spring:url value="{ownerId}/pets/{petId}/edit"
                                            var="editPetUrl">
                                            <spring:param name="ownerId" value="${owner.id}" />
                                            <spring:param name="petId" value="${pet.id}" />
                                        </spring:url>
                                        <a href="${editPetUrl}">Edit Pet</a>
                                    </td>
                                    <td>
                                        <spring:url value="{ownerId}/pets/{petId}/visits/new"
                                            var="addVisitUrl">
                                            <spring:param name="ownerId" value="${owner.id}" />
                                            <spring:param name="petId" value="${pet.id}" />
                                        </spring:url>
                                        <a href="${addVisitUrl}">Add Visit</a>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <jsp:include page="../fragments/footer.jsp" />
    </div>
</body>

</html>