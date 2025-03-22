import { loadProfileMenu } from "./profileMenu.js";
import { getMe } from "./api/users.js";
import { createPost } from "./api/posts.js";

document.addEventListener("DOMContentLoaded", async () => {
	// JWT 이상하면 로그인
	const myInfo = await getMe();
	if (myInfo.message.startsWith("JWT")) {
		document.location.href = "Log in.html";
	}

	// DOM 요소 가져오기
	const title = document.getElementById("titleInput");
	const content = document.getElementById("contentInput");
	const helper = document.getElementById("helper");
	const completeButton = document.getElementById("completeButton");
	const fileInput = document.querySelector("input[type='file']");
	const showImage = document.getElementById("showImage");
	const header = document.getElementById("headerContents");
	const backward = document.getElementById("backward");
	let uploadedImage = "";

	// 프로필 사진 업데이트
	const profileImageUpdate = async () => {
		const profileImage = myInfo.data.profileImage;
		const profilePic = document.createElement("img");
		profilePic.id = "profilePic";
		profilePic.src = profileImage ? profileImage : "./profile_img.webp";
		profilePic.style.width = "30px";
		profilePic.style.height = "30px";
		profilePic.style.borderRadius = "50%";
		header.appendChild(profilePic);
	};

	// 프로필 사진 업데이트 후 프로필 메뉴 만드는 비동기처리
	profileImageUpdate().then(loadProfileMenu);

	// 제목은 26자까지허용
	title.addEventListener("input", () => {
		title.value = title.value.substring(0, 26);
	});

	// 제목, 내용이 차있으면 제출 버튼활성화
	title.addEventListener("focusout", () => {
		completeButton.style.backgroundColor = buttonColor(
			title.value,
			content.value
		);
	});

	content.addEventListener("focusout", () => {
		completeButton.style.backgroundColor = buttonColor(
			title.value,
			content.value
		);
	});

	// ------ 여기부터 파일 업로드 처리할준비 ------

	// 파일 업로드 처리
	fileInput.addEventListener("change", function () {
		const file = fileInput.files[0];

		if (file) {
			const reader = new FileReader();
			reader.onload = function (e) {
				uploadedImage = e.target.result; // 이미지 데이터를 base64로 저장
				showImage.innerHTML = `<img src="${uploadedImage}" width="200" alt="업로드 이미지" />`;
			};
			reader.readAsDataURL(file);
		}
	});

	completeButton.addEventListener("click", async () => {
		helper.innerText = validateTitleAndContent(title.value, content.value);

		if (!helper.innerText) {
			const post = {
				title: title.value,
				contents: content.value,
				image: uploadedImage,
			};
			await createPost(post).then((res) => console.log(res));
			document.location.href = "Posts.html";
		}
	});

	backward.addEventListener("click", () => {
		window.location.href = "Posts.html";
	});
});

// 제목, 좋아요, 댓글, 조회수, 날짜, 내용, 이미지

const validateTitleAndContent = (title, content) => {
	if (!title || !content) return "*제목 내용을 모두 작성해주세요.";

	return "";
};

const buttonColor = (title, content) => {
	if (!title || !content) return "#aca0eb";
	else {
		return "#7f6aee";
	}
};
