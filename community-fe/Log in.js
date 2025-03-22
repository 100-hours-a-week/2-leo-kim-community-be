import { login } from "./api/users.js";

document.addEventListener("DOMContentLoaded", () => {
	const email = document.getElementById("email");
	const password = document.getElementById("password");
	const helper = document.getElementById("helper");
	const loginButton = document.getElementById("loginButton");

	email.addEventListener("focusout", () => {
		helper.innerText = validateEmailPassword(email.value, password.value);
		changeBtnColor();
	});

	password.addEventListener("focusout", () => {
		helper.innerText = validateEmailPassword(email.value, password.value);
		changeBtnColor();
	});

	const changeBtnColor = () => {
		if (helper.innerText.trim() == "") {
			loginButton.style.backgroundColor = "#7f6aee";
			loginButton.disabled = false;
		} else {
			loginButton.style.backgroundColor = "#aca0eb";
			loginButton.disabled = true;
		}
	};

	const loginCallback = async () => {
		const requestBody = {
			email: email.value,
			password: password.value,
		};
		const responseData = await login(JSON.stringify(requestBody));
		if (responseData.message == "INVALID_EMAIL") {
			helper.innerText = "아이디를 확인해주세요";
		} else if (responseData.message == "INVALID_PASSWORD") {
			helper.innerText = "비밀번호를 확인해주세요";
		} else {
			// const profileImage = responseData.data.profileImage;
			document.location.href = "Posts.html";
		}
	};

	loginButton.addEventListener("click", loginCallback);

	document.addEventListener("keyup", async (event) => {
		if (event.key === "Enter") {
			event.preventDefault();
			await loginCallback();
		}
	});
});

const validateEmailPassword = (email, password) => {
	const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,}$/;
	if (!emailRegex.test(email)) {
		return "*올바른 이메일 주소 형식을 입력해주세요.(예:example@example.com)";
	}

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
