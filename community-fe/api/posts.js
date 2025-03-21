import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/posts";

export async function getPosts(page, size) {
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
}
