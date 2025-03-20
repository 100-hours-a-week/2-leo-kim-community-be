import CONFIG from "../config.js";

const baseURL = CONFIG.BACKEND_URL + "/users";

export async function signup(request) {
	const response = await fetch(baseURL + "/signup", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: request,
	});
	const responseData = await response.json();
	console.log(responseData);
	return responseData;
}

export async function login(request) {
	const response = await fetch(baseURL, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: request,
	});
}
