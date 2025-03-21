import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/posts";
const commentURL = CONFIG.BACKEND_URL + "/comments";

export const createComment = async (postIdx, contents) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	console.log(postIdx + " " + contents);

	const response = await fetch(`${baseURL}/${postIdx}/comments`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		body: JSON.stringify({
			contents,
		}),
		credentials: "include",
	});

	console.log(response);

	return response;
};

export const updateComment = async (commentId, newContents) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${commentURL}/${commentId}`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		body: JSON.stringify({
			contents: newContents,
		}),
		credentials: "include",
	});

	console.log(response);

	return response;
};

export const deleteComment = async (commentId) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${commentURL}/${commentId}`, {
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
