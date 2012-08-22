<%
    String redirectURL = "test";
    String redirect = response.encodeRedirectURL(redirectURL);
    response.sendRedirect(redirect);
%>