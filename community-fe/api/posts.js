import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/posts";

export const getPosts = async (page, size) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const query = new URLSearchParams({ page, size }).toString();

	const response = await fetch(`${baseURL}?${query}`, {
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

export const getPostDetail = async (postId) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${baseURL}/${postId}`, {
		method: "GET",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
	});

	const responseData = await response.json();
	console.log(responseData);
	return responseData;
};

export const createPost = async (requestData, uploadedImage) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const formData = new FormData();

	// JSON 데이터를 Blob으로 감싸서 전송
	formData.append(
		"data",
		new Blob([JSON.stringify(requestData)], { type: "application/json" })
	);

	if (uploadedImage) {
		formData.append("postImage", uploadedImage);
	}

	const response = await fetch(`${baseURL}`, {
		method: "POST",
		headers: {
			Authorization: accessToken,
			refreshToken,
		},
		credentials: "include",
		body: formData,
	});

	const responseData = await response.json();
	return responseData;
};

export const updatePost = async (requestData, postId, uploadedImage) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const formData = new FormData();

	// JSON 데이터를 Blob으로 감싸서 전송
	formData.append(
		"data",
		new Blob([JSON.stringify(requestData)], { type: "application/json" })
	);

	if (uploadedImage) {
		formData.append("postImage", uploadedImage);
	}

	const response = await fetch(`${baseURL}/${postId}`, {
		method: "PUT",
		headers: {
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
		body: formData,
	});

	console.log(response);
	return response;
};

export const deletePost = async (postId) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${baseURL}/${postId}`, {
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
