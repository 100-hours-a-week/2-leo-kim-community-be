import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/users";

export const signup = async (request) => {
	const response = await fetch(baseURL + "/signup", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: request,
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
	const accessToken = response.headers.get("Authorization");
	const refreshToken = response.headers.get("refreshToken");
	sessionStorage.setItem("accessToken", accessToken);
	sessionStorage.setItem("refreshToken", refreshToken);
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
