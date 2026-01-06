<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html lang="en">

<head>
    <jsp:include page="fragments/staticFiles.jsp" />
</head>

<body>
    <jsp:include page="fragments/bodyHeader.jsp" />

    <div class="container-fluid xd-container">
        <div class="row justify-content-center">
            <div class="col-md-8 text-center">
                <h2 class="mb-4">Welcome to Spring Petclinic</h2>
                <div class="card shadow-sm border-0">
                    <div class="card-body">
                        <img class="img-fluid" src="<spring:url value="/resources/images/pets.png"
                            htmlEscape="true" />" alt="Pets"/>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="fragments/footer.jsp" />
    </div>

</body>

</html>