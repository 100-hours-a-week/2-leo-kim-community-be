import { getMe } from "./api/users.js";
import { loadProfileMenu } from "./profileMenu.js";
import { getPosts } from "./api/posts.js";
import CONFIG from "./config.js";

let page = 0;
const size = 5;
let isLoading = false;
let isLastPage = false;

document.addEventListener("DOMContentLoaded", async () => {
	const createButton = document.getElementById("postButton");
	const header = document.getElementById("headerContents");

	// JWT 이상하면 로그인
	const myInfo = await getMe();
	if (myInfo.message.startsWith("JWT")) {
		document.location.href = "Log in.html";
	}

	// 프로필 사진 업데이트
	const profileImageUpdate = async () => {
		const profileImage = myInfo.data.profileImage;
		const profilePic = document.createElement("img");
		profilePic.id = "profilePic";
		profilePic.src = profileImage
			? CONFIG.BACKEND_ROOT_URL + profileImage
			: "./profile_img.webp";
		profilePic.style.width = "30px";
		profilePic.style.height = "30px";
		profilePic.style.borderRadius = "50%";
		header.appendChild(profilePic);
	};

	// 프로필 사진 업데이트 후 프로필 메뉴 만드는 비동기처리
	profileImageUpdate().then(loadProfileMenu);

	// 게시물들 렌더링

	window.addEventListener("scroll", handleScroll);
	renderPosts();

	// 게시물 생성 이벤트리스너
	createButton.addEventListener("click", () => {
		document.location.href = "make post.html";
	});

	// 생성 버튼 호버색 처리
	createButton.addEventListener("mouseenter", () => {
		createButton.style.backgroundColor = "#7F6AEE";
		createButton.style.borderColor = "#7F6AEE";
	});

	createButton.addEventListener("mouseleave", () => {
		createButton.style.backgroundColor = "#ACA0EB";
		createButton.style.borderColor = "#ACA0EB";
	});
});

const renderPosts = async () => {
	const loadInitialPosts = async () => {
		isLoading = true;
		const postList = await getPosts(page, size);
		addPostList(postList.data);
		isLoading = false;
	};

	await loadInitialPosts();
};

// postlist 렌더링하는 함수
const addPostList = (postList) => {
	const posts = document.getElementById("posts");
	for (let i = 0; i < postList.length; i++) {
		const item = postList[i];
		console.log(item);

		// 1000 넘으면 k 붙이는 함수
		const refactorNumber = (target) => {
			return target >= 1000 ? target / 1000 + "k" : target;
		};

		const postDiv = document.createElement("div");
		postDiv.classList.add("post");
		postDiv.innerHTML = `
		<div class="postHeader">
			<h2 class="postTitle">${item.title}</h2>
			<div class="postInfo">
				<span class="postData">
				좋아요 ${refactorNumber(item.likes)}
				댓글 ${refactorNumber(item.comments)}
				조회수 ${refactorNumber(item.views)}</span>
				<span class="postDate">${item.regDate}</span>
			</div>
		</div>
		<hr class="line"/>
		<div class="author">
		<img src=${
			item.user.profileImage
				? encodeURI(CONFIG.BACKEND_ROOT_URL + item.user.profileImage)
				: "./profile_img.webp"
		} class="authorProfile"/>
		${item.user.nickname}
		</div>
	`;

		postDiv.addEventListener("click", () => {
			localStorage.setItem("selectedPostIdx", item.postId);
			document.location.href = "post.html";
		});

		posts.appendChild(postDiv);
	}
};

const renderPostList = async (page, size) => {
	try {
		const postList = await getPosts(page, size);

		if (!postList || postList.data.length === 0) {
			isLastPage = true;
			window.removeEventListener("scroll", handleScroll);
			return;
		}

		addPostList(postList.data);
	} catch (err) {
		console.error("게시물 로딩 실패:", err);
		isLastPage = true;
		window.removeEventListener("scroll", handleScroll);
	}
};

const handleScroll = async () => {
	if (isLoading || isLastPage) return;

	// 스크롤이 바닥에서 100px 이내일 때
	if (
		window.innerHeight + window.scrollY >=
		document.body.offsetHeight - 100
	) {
		isLoading = true;
		page += 1;
		await renderPostList(page, size);
		isLoading = false;
	}
};
