export async function loadProfileMenu() {
	const profilePic = document.getElementById("profilePic");

	// 메뉴 요소 생성
	const menu = document.createElement("div");
	menu.id = "profileMenu";
	menu.className = "menuList";

	// 메뉴 항목 추가
	menu.innerHTML = `
			<ul class="menuListItems" style="list-style:none; padding:0; margin:0;">
				<div id="modifyInfo">회원정보수정</div>
				<div id="modifyPw">비밀번호수정</div>
				<div id="logout">로그아웃</div>
			</ul>
		`;

	document.body.appendChild(menu);

	// 프로필 클릭 시 메뉴 토글
	profilePic.addEventListener("click", (event) => {
		event.stopPropagation(); // 클릭 이벤트 전파 방지
		menu.style.display = menu.style.display === "none" ? "block" : "none";
	});

	// 로그아웃 버튼 클릭 시
	document.getElementById("logout").addEventListener("click", () => {
		sessionStorage.removeItem("loginUser");
		document.location.href = "Log in.html";
	});

	document.getElementById("modifyInfo").addEventListener("click", () => {
		document.location.href = "edit profile.html";
	});

	document.getElementById("modifyPw").addEventListener("click", () => {
		document.location.href = "edit password.html";
	});

	// 다른 곳을 클릭하면 메뉴 닫기
	document.addEventListener("click", (event) => {
		if (menu.style.display === "block" && event.target !== profilePic) {
			menu.style.display = "none";
		}
	});
}
