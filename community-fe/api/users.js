import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/users";

export const signup = async (requestData, profileImage) => {
	const formData = new FormData();

	// JSON 데이터를 Blob으로 감싸서 전송
	formData.append(
		"data",
		new Blob([JSON.stringify(requestData)], { type: "application/json" })
	);

	// 이미지 파일도 추가 (없으면 안 보냄)
	if (profileImage) {
		formData.append("profileImage", profileImage);
	}

	const response = await fetch(baseURL + "/signup", {
		method: "POST",
		body: formData,
	});
	const responseData = await response.json();
	return responseData;
};

export const login = async (request) => {
	const response = await fetch(baseURL, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: request,
		credentials: "include",
	});
	const responseData = await response.json();
	console.log(responseData);
	const accessToken = response.headers.get("Authorization");
	const refreshToken = response.headers.get("refreshToken");
	sessionStorage.setItem("accessToken", accessToken);
	sessionStorage.setItem("refreshToken", refreshToken);
	localStorage.setItem("profileImage", responseData.data.profileImage);
	return responseData;
};

export const getMe = async () => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");
	const response = await fetch(baseURL, {
		method: "GET",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
	});

	const responseData = await response.json();
	return responseData;
};

export const updateUser = async (req) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");
	const response = await fetch(baseURL, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
		body: JSON.stringify(req),
	});
	console.log(response);
	return response;
};

export const updateUserPassword = async (newPassword) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");
	const response = await fetch(baseURL + "/password", {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
		body: JSON.stringify({ newPassword }),
	});
	console.log(response);
	return response;
};

export const deleteUser = async () => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");
	const response = await fetch(baseURL, {
		method: "DELETE",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
	});
	console.log(response);
	return response;
};

export const isDuplicateNickname = async (nickname) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");
	const response = await fetch(`${baseURL}/nickname/${nickname}`, {
		method: "GET",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
	});
	return response;
};

// export const getUserById = async (userId) => {};
