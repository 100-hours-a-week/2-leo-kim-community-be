document.addEventListener("DOMContentLoaded", () => {
	const header = document.getElementById("headerContents");
	const password = document.getElementById("password");
	const passwordConfirm = document.getElementById("passwordConfirm");
	const passwordHelper = document.getElementById("helperPassword");
	const passwordConfirmHelper = document.getElementById(
		"helperPasswordConfirm"
	);
	const modifyButton = document.getElementById("modifyButton");
	const tostMessage = document.getElementById("tost_message");

	const loginUser = JSON.parse(sessionStorage.getItem("loginUser"));
	const members = JSON.parse(localStorage.getItem("members"));

	const profilePic = document.createElement("img");
	profilePic.id = "profilePic";
	profilePic.src = loginUser.profile
		? loginUser.profile
		: "./profile_img.webp";
	profilePic.style.width = "30px";
	profilePic.style.height = "30px";
	profilePic.style.borderRadius = "50%";
	header.appendChild(profilePic);

	const updateButtonState = () => {
		if (
			password.value &&
			passwordConfirm.value &&
			passwordHelper.innerText.trim() === "" &&
			passwordConfirmHelper.innerText.trim() === ""
		) {
			modifyButton.disabled = false;
			modifyButton.style.backgroundColor = "#7F6AEE";
		} else {
			modifyButton.disabled = true;
			modifyButton.style.backgroundColor = "#ACAOEB";
		}
	};

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

	modifyButton.addEventListener("click", () => {
		if (modifyButton.style.disabled) return;
		loginUser.password = password.value;
		sessionStorage.setItem("loginUser", JSON.stringify(loginUser));
		for (let i = 0; i < members.length; i++) {
			const item = members[i];
			if (item.email === loginUser.email) {
				item.password = password.value;
			}
			members[i] = item;
			localStorage.setItem("members", JSON.stringify(members));
			break;
		}
		tostMessage.classList.add("active");
		setTimeout(() => {
			tostMessage.classList.remove("active");
		}, 2000);
	});
});

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
