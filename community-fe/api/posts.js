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

export const getPostDetail = async (postIdx) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${baseURL}/${postIdx}`, {
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

export const createPost = async (req) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");
	console.log(req);

	const response = await fetch(`${baseURL}`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
		body: JSON.stringify(req),
	});

	const responseData = await response.json();
	return responseData;
};

export const updatePost = async (req, postId) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${baseURL}/${postId}`, {
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
