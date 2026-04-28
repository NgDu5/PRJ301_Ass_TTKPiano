<%@page import="model.DTO.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    User loggedUser =
        (User) session.getAttribute("loggedUser");
    java.util.Map cart =
        (java.util.Map) session.getAttribute("cart");
    int cartCount = (cart != null) ? cart.size() : 0;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${param.pageTitle != null ? param.pageTitle : 'TTK Piano Music Center'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <div class="header-inner">
        <a class="logo" href="${pageContext.request.contextPath}/courses">
            🎹 TTK * Piano Music Center
        </a>
        <nav>
            <a href="${pageContext.request.contextPath}/courses">Khóa Học</a>
            <% if (loggedUser != null && !loggedUser.isAdmin()) { %>
                
                <a href="${pageContext.request.contextPath}/order-tracking">Theo Dõi Đơn</a>
            <% } %>
            <% if (loggedUser != null && loggedUser.isAdmin()) { %>
                <a href="${pageContext.request.contextPath}/admin/courses">Quản Lý Khóa Học</a>
            <% } %>
            <% if (loggedUser == null || !loggedUser.isAdmin()) { %>
                <a href="${pageContext.request.contextPath}/cart">
                    🛒 Giỏ Hàng
                    <% if (cartCount > 0) { %><span class="badge"><%= cartCount %></span><% } %>
                </a>
            <% } %>
            <% if (loggedUser != null) { %>
                <span class="welcome">Xin chào, <strong><%= loggedUser.getFullName() %></strong></span>
                <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/login">Đăng Nhập</a>
            <% } %>
        </nav>
    </div>
</header>
<div class="container">
