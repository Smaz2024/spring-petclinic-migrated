<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container-fluid">
        <a class="navbar-brand" href="<spring:url value="/" htmlEscape="true" />">
        <span class="bi bi-heart-pulse-fill" style="color: #3d9970;"></span> Spring PetClinic
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar"
            aria-controls="mainNavbar" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link text-uppercase" href="<spring:url value="/" htmlEscape="true" />"
                    title="home page">
                    <span class="bi bi-house-door"></span> Home
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-uppercase" href="<spring:url value="/owners/find"
                        htmlEscape="true" />" title="find owners">
                    <span class="bi bi-search"></span> Find owners
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-uppercase" href="<spring:url value="/vets.html"
                        htmlEscape="true" />" title="veterinarians">
                    <span class="bi bi-list-ul"></span> Veterinarians
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-uppercase" href="<spring:url value="/oups"
                        htmlEscape="true" />" title="trigger exception">
                    <span class="bi bi-exclamation-triangle"></span> Error
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid xd-container">
