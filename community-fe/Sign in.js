import { signup } from "./api/users.js";

document.addEventListener("DOMContentLoaded", () => {
	const email = document.getElementById("email");
	const emailHelper = document.getElementById("emailHelper");
	const password = document.getElementById("password");
	const passwordHelper = document.getElementById("passwordHelper");
	const passwordConfirm = document.getElementById("passwordConfirm");
	const passwordConfirmHelper = document.getElementById(
		"passwordConfirmHelper"
	);
	const nickname = document.getElementById("nickname");
	const nicknameHelper = document.getElementById("nicknameHelper");
	const signInButton = document.getElementById("signInButton");
	const profilePicAdd = document.getElementById("profilePicAdd");
	const profileInput = document.getElementById("profileInput");
	const profilePreview = document.getElementById("profilePreview");
	const backward = document.getElementById("backward");
	let uploadedProfileImage = "";

	// 파일 읽기 후 실행할 함수 (Promise 사용)
	const readFileAsync = (file) => {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();

			reader.onload = function (e) {
				resolve(e.target.result);
			};

			reader.onerror = function () {
				reject(new Error("파일을 읽는 중 오류 발생"));
			};

			reader.readAsDataURL(file);
		});
	};

	// + 버튼 클릭 시 파일 선택창 열기
	profilePicAdd.addEventListener("click", () => {
		profileInput.value = "";
		profileInput.click();
	});

	// 파일 업로드 처리
	profileInput.addEventListener("change", async function () {
		if (!profileInput.files.length) return; // 파일이 선택되지 않으면 실행 안 함

		const file = profileInput.files[0];

		try {
			// **파일 로딩 완료 후 실행**
			uploadedProfileImage = file;
			const uploadedProfileImagePreview = await readFileAsync(file);

			// 미리보기 설정
			profilePreview.innerHTML = `<img src="${uploadedProfileImagePreview}" width="100" alt="프로필 사진" class="profileImage"/>`;
			profilePreview.style.display = "block";

			// 기존 클릭 이벤트 제거 후 다시 추가
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

	// 회원가입 활성화 체크를 위한
	const updateButtonState = () => {
		if (
			email.value &&
			password.value &&
			passwordConfirm.value &&
			nickname.value && // 모든 필드가 채워짐
			emailHelper.innerText.trim() === "" &&
			passwordHelper.innerText.trim() === "" &&
			passwordConfirmHelper.innerText.trim() === "" &&
			nicknameHelper.innerText.trim() === "" // 오류 메시지가 없는지 확인
		) {
			signInButton.disabled = false;
		} else {
			signInButton.disabled = true;
		}
	};

	email.addEventListener("focusout", () => {
		emailHelper.innerText = validateEmail(email.value);
		updateButtonState();
	});

	password.addEventListener("focusout", () => {
		passwordHelper.innerText = validatePassword(password.value);
		updateButtonState();
	});

	passwordConfirm.addEventListener("focusout", () => {
		passwordConfirmHelper.innerText = validatePasswordConfirm(
			password.value,
			passwordConfirm.value
		);
		updateButtonState();
	});

	nickname.addEventListener("focusout", () => {
		nicknameHelper.innerText = validateNickname(nickname.value);
		updateButtonState();
	});

	signInButton.addEventListener("click", async () => {
		const request = {
			email: email.value,
			password: password.value,
			nickname: nickname.value,
		};
		const response = await signup(request, uploadedProfileImage);
		console.log(response.message);
		if (response.message === "DUPLICATE_EMAIL") {
			emailHelper.innerText = "*중복된 이메일 입니다.";
		} else if (response.message === "DUPLICATE_NICKNAME") {
			nicknameHelper.innerText = "*중복된 닉네임 입니다.";
		} else {
			window.location.href = "Log in.html";
		}
	});

	backward.addEventListener("click", () => {
		window.location.href = "Log in.html";
	});
});

const validateEmail = (email) => {
	if (!email) {
		return "*이메일을 입력해주세요.";
	}

	const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,}$/;
	if (!emailRegex.test(email)) {
		return "*올바른 이메일 주소 형식을 입력해주세요.(예:example@example.com)";
	}

	return "";
};

const validatePassword = (password) => {
	if (!password) {
		return "*비밀번호를 입력해주세요.";
	}

	const passwordRegex =
		/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()-_=+])[a-zA-Z0-9!@#$%^&*()-_=+]{8,20}$/;
	if (!passwordRegex.test(password)) {
		return "*비밀번호는 8자 이상 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.";
	}

	return "";
};

const validatePasswordConfirm = (password, passwordConfirm) => {
	if (!passwordConfirm) {
		return "*비밀번호를 한번 더 입력해주세요.";
	}

	if (password != passwordConfirm) {
		return "*비밀번호가 다릅니다.";
	}

	return "";
};

const validateNickname = (nickname) => {
	if (!nickname) {
		return "*닉네임을 입력해주세요.";
	}

	if (/\s/.test(nickname)) {
		return "*띄어쓰기를 없애주세요.";
	}

	if (nickname.length > 10) {
		return "*닉네임은 최대 10자까지 작성 가능합니다.";
	}

	return "";
};
