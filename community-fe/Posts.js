document.addEventListener("DOMContentLoaded", () => {
	const createButton = document.getElementById("postButton");
	const header = document.getElementById("headerContents");
	const loginUser = JSON.parse(sessionStorage.getItem("loginUser"));

	const profilePic = document.createElement("img");
	profilePic.id = "profilePic";
	profilePic.src = loginUser.profile
		? loginUser.profile
		: "./profile_img.webp";
	profilePic.style.width = "30px";
	profilePic.style.height = "30px";
	profilePic.style.borderRadius = "50%";
	header.appendChild(profilePic);

	renderPosts();

	createButton.addEventListener("click", () => {
		document.location.href = "make post.html";
	});

	createButton.addEventListener("mouseenter", () => {
		createButton.style.backgroundColor = "#7F6AEE";
		createButton.style.borderColor = "#7F6AEE";
	});

	createButton.addEventListener("mouseleave", () => {
		createButton.style.backgroundColor = "#ACA0EB";
		createButton.style.borderColor = "#ACA0EB";
	});
});

const renderPosts = () => {
	const posts = document.getElementById("posts");
	const postList = JSON.parse(localStorage.getItem("posts")) || [];
	for (let i = 0; i < postList.length; i++) {
		const item = postList[i];

		// innerHTML += 대신 createElement()를 사용하여 XSS 방지 & 성능 개선하는게 좋다
		let [likes, comments, views] = [
			Object.keys(item.likes).length,
			Object.keys(item.comments).length,
			item.views,
		];
		if (likes >= 1000) {
			likes = likes / 1000 + "k";
		}
		if (comments >= 1000) {
			comments = comments / 1000 + "k";
		}
		if (views >= 1000) {
			views = views / 1000 + "k";
		}

		const postDiv = document.createElement("div");
		postDiv.classList.add("post");
		postDiv.innerHTML = `
            <div class="postHeader">
                <h2 class="postTitle">${item.title}</h2>
                <div class="postInfo">
                    <span class="postData">좋아요 ${likes} 댓글 ${comments} 조회수 ${views}</span>
                    <span class="postDate">${item.date}</span>
                </div>
            </div>
            <hr class="line"/>
            <div class="author">${item.nickname}</div>
        `;

		postDiv.addEventListener("click", () => {
			localStorage.setItem("selectedPostIdx", i);
			document.location.href = "post.html";
		});

		posts.appendChild(postDiv);
	}
};

// 제목, 좋아요, 댓글, 조회수, 날짜, 내용, 이미지
