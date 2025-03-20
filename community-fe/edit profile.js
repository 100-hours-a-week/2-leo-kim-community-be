document.addEventListener("DOMContentLoaded", () => {
	const email = document.getElementById("email");
	const profilePreview = document.getElementById("profilePreview");
	const nickname = document.getElementById("nickname");
	const nicknameHelper = document.getElementById("nicknameHelper");
	const confirmButton = document.getElementById("confirmButton");
	const signOutButton = document.getElementById("signOut");
	const signOutModal = document.getElementById("signOutModal");
	const confirmSignOut = document.getElementById("confirmSignOut");
	const cancelSignOut = document.getElementById("cancelSignOut");
	const header = document.getElementById("headerContents");
	const tostMessage = document.getElementById("tost_message");
	const profilePicAdd = document.getElementById("profilePicAdd");
	const profileInput = document.getElementById("profileInput");

	const loginUser = JSON.parse(sessionStorage.getItem("loginUser"));
	const members = JSON.parse(localStorage.getItem("members"));
	const posts = JSON.parse(localStorage.getItem("posts"));

	let uploadedProfileImage = loginUser.profile;

	email.innerText = loginUser.email;
	nickname.value = loginUser.nickname;

	// ✅ 프로필 미리보기를 업데이트하는 함수
	const updateProfilePreview = (imageSrc) => {
		console.log("updateProfilePreview 실행!", imageSrc);

		// 🔹 프로필 미리보기 영역 업데이트
		profilePreview.innerHTML = ""; // 기존 내용 제거
		const imgPreview = document.createElement("img");
		imgPreview.src = imageSrc;
		imgPreview.width = 100;
		imgPreview.alt = "프로필 사진";
		imgPreview.classList.add("profileImage");
		profilePreview.appendChild(imgPreview);

		// 🔹 헤더 프로필 이미지 업데이트
		const profilePic = document.createElement("img");
		profilePic.id = "profilePic";
		profilePic.src = imageSrc;
		profilePic.style.width = "30px";
		profilePic.style.height = "30px";
		profilePic.style.borderRadius = "50%";

		// 기존 헤더 이미지 삭제 후 추가 (중복 방지)
		const existingProfilePic = document.getElementById("profilePic");
		if (existingProfilePic) existingProfilePic.remove();
		header.appendChild(profilePic);
	};

	// ✅ 초기 로그인 시 프로필 반영
	if (loginUser.profile) {
		updateProfilePreview(loginUser.profile);
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
			uploadedProfileImage = await readFileAsync(file);
			console.log("프로필 이미지 데이터 URL:", uploadedProfileImage);

			// ✅ 기존 이미지 변경 (직접 `src` 속성 변경)
			profilePreview.innerHTML = ""; // 기존 내용 제거
			const img = document.createElement("img");
			img.src = uploadedProfileImage;
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

	nickname.addEventListener("focusout", () => {
		nicknameHelper.innerText = validateNickname(nickname.value, loginUser);
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

	confirmButton.addEventListener("click", () => {
		if (confirmButton.disabled) return;

		loginUser.nickname = nickname.value;
		loginUser.profile = uploadedProfileImage;
		sessionStorage.setItem("loginUser", JSON.stringify(loginUser));

		for (let i = 0; i < members.length; i++) {
			const item = members[i];
			if (item.email === loginUser.email) {
				item.nickname = nickname.value;
				item.profile = uploadedProfileImage;
				members[i] = item;
				break;
			}
		}

		localStorage.setItem("members", JSON.stringify(members));

		tostMessage.classList.add("active");
		setTimeout(() => {
			tostMessage.classList.remove("active");
		}, 2000);
	});

	signOutButton.addEventListener("click", () => {
		signOutModal.style.display = "block";
	});

	cancelSignOut.addEventListener("click", () => {
		signOutModal.style.display = "none";
	});

	confirmSignOut.addEventListener("click", () => {
		sessionStorage.removeItem("loginUser");
		const newPosts = posts.filter(
			(post) => post.author !== loginUser.email
		);
		const newMembers = members.filter(
			(member) => member.email !== loginUser.email
		);
		localStorage.setItem("members", JSON.stringify(newMembers));
		localStorage.setItem("posts", JSON.stringify(newPosts));
		window.location.href = "Log in.html";
	});
});

const validateNickname = (nickname, loginUser) => {
	if (!nickname) {
		return "*닉네임을 입력해주세요.";
	}

	if (/\s/.test(nickname)) {
		return "*띄어쓰기를 없애주세요.";
	}

	if (nickname.length > 10) {
		return "*닉네임은 최대 10자까지 작성 가능합니다.";
	}

	const members = JSON.parse(localStorage.getItem("members")) || [];
	for (let i = 0; i < members.length; i++) {
		const item = members[i];
		if (!item.email) continue;
		if (loginUser.nickname != nickname && item.nickname === nickname)
			return "*중복된 닉네임 입니다.";
	}

	return "";
};
