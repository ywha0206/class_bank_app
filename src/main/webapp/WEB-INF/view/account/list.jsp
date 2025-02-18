<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- header.jsp -->
<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<!-- start of content.jsp(메인 영역)   -->
<div class="col-sm-8">
	<h2>계좌 목록(인증)</h2>
	<h5>Bank App 오신 걸 환영합니다.</h5>
		<c:choose>
		<c:when test="${accountList != null }">
			<table class="table">
				<thead>
					<tr>
						<th>계좌 번호</th>
						<th>잔액</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="account" items="${accountList}">
						<tr>
							<td><a href="/account/detail/${account.id}?type=all">${account.number}</a></td>
							<td>${account.balance}</td>
						</tr>
					</c:forEach>

				</tbody>
			</table>
		</c:when>
		<c:otherwise>
		<div class="jumbotron display-4">
			<h5><small>아직 생성된 계좌가 없습니다</small></h5>
		</div>
		</c:otherwise>
	</c:choose>
</div>
</div>
</div>

<!-- end of content.jsp(메인 영역)   -->

<!-- footer.jsp -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>


