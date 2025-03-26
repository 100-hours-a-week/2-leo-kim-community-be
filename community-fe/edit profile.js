import {
	deleteUser,
	getMe,
	isDuplicateNickname,
	updateUser,
} from "./api/users.js";
import { loadProfileMenu } from "./profileMenu.js";
import CONFIG from "./config.js";

document.addEventListener("DOMContentLoaded", async () => {
	// JWT 이상하면 로그인
	const myInfo = await getMe();
	console.log(myInfo);
	if (myInfo.message.startsWith("JWT")) {
		document.location.href = "Log in.html";
	}

	// 요소 grab
	const email = document.getElementById("email");
	const profilePreview = document.getElementById("profilePreview");
	const nickname = document.getElementById("nickname");
	const nicknameHelper = document.getElementById("nicknameHelper");
	const confirmButton = document.getElementById("confirmButton");
	const header = document.getElementById("headerContents");
	const tostMessage = document.getElementById("tost_message");
	const profilePicAdd = document.getElementById("profilePicAdd");
	const profileInput = document.getElementById("profileInput");
	const signOutModal = document.getElementById("signOutModal");
	const signOut = document.getElementById("signOut");
	const cancelSignOut = document.getElementById("cancelSignOut");
	const confirmSignOut = document.getElementById("confirmSignOut");

	// 초기 렌더링
	let uploadedProfileImage = myInfo.data.profileImage;
	email.innerText = myInfo.data.email;
	nickname.value = myInfo.data.nickname;

	// 프로필 사진 업데이트
	const profileImageUpdate = async () => {
		const profileImage = myInfo.data.profileImage;
		const profilePic = document.createElement("img");
		profilePic.id = "profilePic";
		profilePic.src = profileImage
			? CONFIG.BACKEND_ROOT_URL + profileImage
			: "./profile_img.webp";
		profilePic.style.width = "30px";
		profilePic.style.height = "30px";
		profilePic.style.borderRadius = "50%";
		header.appendChild(profilePic);
	};

	// 프로필 사진 업데이트 후 프로필 메뉴 만드는 비동기처리
	profileImageUpdate().then(loadProfileMenu);

	// 프로필 미리보기를 업데이트하는 함수
	const updateProfilePreview = (imageSrc) => {
		console.log("updateProfilePreview 실행!", imageSrc);

		// 🔹 프로필 미리보기 영역 업데이트
		profilePreview.innerHTML = ""; // 기존 내용 제거
		const imgPreview = document.createElement("img");
		imgPreview.src = CONFIG.BACKEND_ROOT_URL + imageSrc;
		imgPreview.width = 100;
		imgPreview.alt = "프로필 사진";
		imgPreview.classList.add("profileImage");
		profilePreview.appendChild(imgPreview);
	};

	// 초기 로그인 시 프로필 반영
	if (uploadedProfileImage) {
		updateProfilePreview(uploadedProfileImage);
	}

	// 파일 읽기 후 실행할 함수 (Promise 사용)
	const readFileAsync = (file) => {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();

			reader.onload = function (e) {
				console.log("파일 로딩 완료:", e.target.result);
				resolve(e.target.result);
			};

			reader.onerror = function (error) {
				console.error("파일 읽기 오류:", error);
				reject(new Error("파일을 읽는 중 오류 발생"));
			};

			reader.readAsDataURL(file);
		});
	};

	profilePreview.addEventListener("click", () => {
		profileInput.value = "";
		profileInput.click();
	});

	// 파일 업로드 처리
	profileInput.addEventListener("change", async function () {
		if (!profileInput.files.length) return; // 파일이 선택되지 않으면 실행 안 함

		const file = profileInput.files[0];
		console.log("선택된 파일:", file);

		try {
			// **파일 로딩 완료 후 실행**
			uploadedProfileImage = file;
			const uploadedProfileImagePreview = await readFileAsync(file);

			console.log("프로필 이미지 데이터 URL:", uploadedProfileImage);

			// ✅ 기존 이미지 변경 (직접 `src` 속성 변경)
			profilePreview.innerHTML = ""; // 기존 내용 제거
			const img = document.createElement("img");
			img.src = uploadedProfileImagePreview;
			img.width = 100;
			img.alt = "프로필 사진";
			img.classList.add("profileImage");
			profilePreview.appendChild(img);

			// ✅ display 속성 수정 (안보일 경우)
			profilePreview.style.display = "block";

			// ✅ 기존 클릭 이벤트 제거 후 다시 추가
			profilePreview.removeEventListener("click", handleProfileClick);
			profilePreview.addEventListener("click", handleProfileClick);
		} catch (error) {
			console.error("파일을 로드하는 중 오류 발생:", error);
		}
	});

	const handleProfileClick = () => {
		profileInput.value = ""; // 같은 파일 다시 선택 가능하도록 초기화
		profileInput.click();
	};

	// 닉네임 정당성 체크
	nickname.addEventListener("focusout", async () => {
		nicknameHelper.innerText = await validateNickname(nickname.value);
		updateButtonState();
	});

	const updateButtonState = () => {
		if (
			nickname.value && // 모든 필드가 채워짐
			nicknameHelper.innerText.trim() === "" // 오류 메시지가 없는지 확인
		) {
			confirmButton.disabled = false;
		} else {
			confirmButton.disabled = true;
		}
	};

	confirmButton.addEventListener("click", async () => {
		if (confirmButton.disabled) return;

		const requestData = {
			nickname: nickname.value,
		};

		const response = await updateUser(requestData, uploadedProfileImage);

		tostMessage.classList.add("active");
		setTimeout(() => {
			tostMessage.classList.remove("active");
		}, 2000);
	});

	signOut.addEventListener("click", () => {
		console.log("click!");
		signOutModal.style.display = "flex";
		cancelSignOut.addEventListener("click", () => {
			signOutModal.style.display = "none";
		});

		confirmSignOut.addEventListener("click", async () => {
			await deleteUser();
			document.location.href = "Log in.html";
		});
	});
});

const validateNickname = async (nickname) => {
	if (!nickname) {
		return "*닉네임을 입력해주세요.";
	}

	if (/\s/.test(nickname)) {
		return "*띄어쓰기를 없애주세요.";
	}

	if (nickname.length > 10) {
		return "*닉네임은 최대 10자까지 작성 가능합니다.";
	}

	const res = await isDuplicateNickname(nickname);
	if (!res.ok) return "*중복된 닉네임입니다.";

	return "";
};
