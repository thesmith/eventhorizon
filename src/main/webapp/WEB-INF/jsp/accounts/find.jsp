<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<form:form action="" modelAttribute="account">
    <table>
        <tr>
            <td>Username:</td>
            <td><form:input path="username" /></td>
        </tr>
        <tr>
            <td>UserId:</td>
            <td><form:input path="userId" /></td>
        </tr>
        <tr>
            <td>Template:</td>
            <td><form:input path="template" /></td>
        </tr>
        <tr>
            <td></td>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form:form>