import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/users";

export let accessToken = "";
export let refreshToken = "";

export async function signup(request) {
	const response = await fetch(baseURL + "/signup", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: request,
	});
	const responseData = await response.json();
	return responseData;
}

export async function login(request) {
	const response = await fetch(baseURL, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: request,
		credentials: "include",
	});
	const responseData = await response.json();
	accessToken = response.headers.get("Authorization");
	refreshToken = response.headers.get("refreshToken");
	return responseData;
}
