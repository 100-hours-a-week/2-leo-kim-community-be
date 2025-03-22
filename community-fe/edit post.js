import { getMe } from "./api/users.js";
import { getPostDetail, updatePost } from "./api/posts.js";

document.addEventListener("DOMContentLoaded", async () => {
	// JWT 이상하면 로그인
	const myInfo = await getMe();
	if (myInfo.message.startsWith("JWT")) {
		document.location.href = "Log in.html";
	}

	// 요소 grab
	const title = document.getElementById("titleInput");
	const content = document.getElementById("contentInput");
	const helper = document.getElementById("helper");
	const completeButton = document.getElementById("completeButton");
	const fileInput = document.querySelector("input[type='file']");
	const showImage = document.getElementById("showImage");

	// 변수 grab
	const postId = JSON.parse(localStorage.getItem("selectedPostIdx"));
	const res = await getPostDetail(postId);
	const post = res.data;
	let uploadedImage = post.image || "";

	title.value = post.title;
	content.value = post.contents;

	title.addEventListener("input", () => {
		title.value = title.value.substring(0, 26);
	});

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
			const req = {
				title: title.value,
				contents: content.value,
				image: uploadedImage,
			};
			console.log(req);
			await updatePost(req, postId);
			document.location.href = "Posts.html";
		}
	});
});

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
