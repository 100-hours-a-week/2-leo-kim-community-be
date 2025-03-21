import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/posts";

export const toggleLike = async (postIdx) => {
	const accessToken = sessionStorage.getItem("accessToken");
	const refreshToken = sessionStorage.getItem("refreshToken");

	const response = await fetch(`${baseURL}/${postIdx}/like`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			Authorization: accessToken,
			refreshToken: refreshToken,
		},
		credentials: "include",
	});

	return response;
};
