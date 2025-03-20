document.addEventListener("DOMContentLoaded", () => {
	// DOM 요소 가져오기
	const backward = document.getElementById("backward");
	const title = document.getElementById("title");
	const author = document.getElementById("author");
	const date = document.getElementById("date");
	const picture = document.getElementById("picture");
	const likes = document.getElementById("likes");
	const views = document.getElementById("views");
	const contents = document.getElementById("contents");
	const commentNums = document.getElementById("commentNums");
	const comments = document.getElementById("comments");
	let commentBtn = document.getElementById("commentBtn");
	const commentContent = document.getElementById("commentContent");
	const buttonsContainer = document.querySelector(".buttons");
	const headerContents = document.getElementById("headerContents");
	const authorProfile = document.getElementById("authorProfile");

	// 데이터 가져오기
	const loginUser = JSON.parse(sessionStorage.getItem("loginUser"));
	const postIdx = localStorage.getItem("selectedPostIdx");
	const posts = JSON.parse(localStorage.getItem("posts"));
	const post = posts[postIdx];

	const profilePic = document.createElement("img");
	profilePic.id = "profilePic";
	profilePic.src = loginUser.profile
		? loginUser.profile
		: "./profile_img.webp";
	profilePic.style.width = "30px";
	profilePic.style.height = "30px";
	profilePic.style.borderRadius = "50%";
	headerContents.appendChild(profilePic);

	const buttonsSetup = () => {
		const modifyButton = document.getElementById("modifyBtn");
		modifyButton.addEventListener("click", () => {
			document.location.href = "edit post.html";
		});
	};

	if (post.author === loginUser.email) {
		buttonsContainer.innerHTML = `<button class="modify" id="modifyBtn">수정</button>
        <button class="delete" id="deleteBtn">삭제</button> 
        `;
		buttonsSetup();
	}

	// 뒤로가기
	backward.addEventListener("click", () => {
		document.location.href = "Posts.html";
	});

	// 게시글 정보 표시
	console.log(post.profilePic);
	authorProfile.innerHTML = `<image src=${post.profilePic}>`;
	authorProfile.style.marginRight = "10px";
	title.innerText = post.title;
	author.innerText = post.nickname;
	date.innerText = post.date;
	contents.innerText = post.contents;
	if (post.image) {
		picture.src = post.image;
	}

	// 게시글 정보 렌더링 (조회수, 좋아요, 댓글수)
	const renderPosts = () => {
		likes.innerHTML = `<div>${post.likes.length}</div><div>좋아요 수</div>`;
		views.innerHTML = `<div>${post.views}</div><div>조회수</div>`;
		commentNums.innerHTML = `<div>${post.comments.length}</div><div>댓글</div>`;
	};

	const increaseViews = () => {
		post.views++;
		posts[postIdx] = post;
		localStorage.setItem("posts", JSON.stringify(posts));
	};

	// 좋아요 상태 렌더링
	const renderLikes = () => {
		const liked = post.likes.some(
			(likedOne) => likedOne === loginUser.email
		);
		likes.style.backgroundColor = liked ? "#ACA0EB" : "#D9D9D9";
	};

	// 초기 렌더링
	increaseViews();
	renderPosts();
	renderLikes();

	// 좋아요 버튼 클릭
	likes.addEventListener("click", () => {
		if (likes.style.backgroundColor === "rgb(217, 217, 217)") {
			post.likes.push(loginUser.email);
		} else {
			const index = post.likes.findIndex(
				(likedOne) => likedOne === loginUser.email
			);
			if (index !== -1) {
				post.likes.splice(index, 1);
			}
		}
		posts[postIdx] = post;
		localStorage.setItem("posts", JSON.stringify(posts));
		renderLikes();
		renderPosts();
	});

	// 댓글 입력 시 버튼 색상 변경
	commentContent.addEventListener("input", () => {
		commentBtn.style.backgroundColor = commentContent.value
			? "#7F6AEE"
			: "#ACA0EB";
	});

	// 기본 댓글 추가 핸들러
	const setupDefaultCommentHandler = (button) => {
		button.addEventListener("click", () => {
			if (commentContent.value) {
				const date = new Date();
				const dateTimeString =
					date.toDateString() + " " + date.toLocaleTimeString();
				const addedComment = {
					author: loginUser.email,
					authorNickname: loginUser.nickname,
					date: dateTimeString,
					content: commentContent.value,
				};
				post.comments.push(addedComment);
				posts[postIdx] = post;
				localStorage.setItem("posts", JSON.stringify(posts));
				commentContent.value = "";
				comments.innerHTML = renderComments(post.comments);
				renderPosts();
			}
		});
	};

	// 댓글 수정 핸들러
	const editComment = (index) => {
		commentContent.value = post.comments[index].content;
		commentBtn.innerText = "댓글 수정";

		// 기존 이벤트 리스너 제거
		const newBtn = commentBtn.cloneNode(true);
		commentBtn.parentNode.replaceChild(newBtn, commentBtn);
		commentBtn = newBtn;

		// 수정용 이벤트 리스너 추가
		commentBtn.addEventListener("click", function handleEdit() {
			if (commentContent.value) {
				post.comments[index].content = commentContent.value;
				localStorage.setItem("posts", JSON.stringify(posts));
				commentContent.value = "";

				// 버튼 초기화
				commentBtn.innerText = "댓글 등록";
				const resetBtn = commentBtn.cloneNode(true);
				commentBtn.parentNode.replaceChild(resetBtn, commentBtn);
				commentBtn = resetBtn;

				// 기본 댓글 등록 이벤트 다시 설정
				setupDefaultCommentHandler(commentBtn);

				// UI 업데이트
				comments.innerHTML = renderComments(post.comments);
			}
		});
	};

	// 댓글 삭제
	const deleteComment = (index) => {
		post.comments.splice(index, 1);
		posts[postIdx] = post;
		localStorage.setItem("posts", JSON.stringify(posts));
		comments.innerHTML = renderComments(post.comments);
		renderPosts();
	};

	// 초기 댓글 핸들러 설정
	setupDefaultCommentHandler(commentBtn);

	// 초기 댓글 목록 표시
	comments.innerHTML = renderComments(post.comments);

	// 댓글 수정/삭제 버튼 이벤트
	comments.addEventListener("click", (event) => {
		if (event.target.classList.contains("deleteButton")) {
			selectedCommentIndex = event.target.dataset.index;
			deleteModalComment.style.display = "flex";
		}
		if (event.target.classList.contains("fixButton")) {
			editComment(event.target.dataset.index);
		}
	});

	const deleteButton = document.getElementById("deleteBtn");
	deleteButton.addEventListener("click", () => {
		deleteModalPost.style.display = "flex";
	});

	// 삭제 모달 관련
	const deleteModalComment = document.getElementById("deleteModalComment");
	const cancelButton = document.getElementById("cancelButton");
	const confirmDeleteButton = document.getElementById("confirmDeleteButton");
	const deleteModalPost = document.getElementById("deleteModalPost");
	const cancelButtonPost = document.getElementById("cancelButtonPost");
	const confirmDeleteButtonPost = document.getElementById(
		"confirmDeleteButtonPost"
	);

	let selectedCommentIndex = null;

	deleteButton.addEventListener("click", () => {
		deleteModalPost.style;
	});

	cancelButton.addEventListener("click", () => {
		deleteModalComment.style.display = "none";
		selectedCommentIndex = null;
	});

	confirmDeleteButton.addEventListener("click", () => {
		if (selectedCommentIndex !== null) {
			deleteComment(selectedCommentIndex);
			renderPosts();
		}
		deleteModalComment.style.display = "none";
	});

	cancelButtonPost.addEventListener("click", () => {
		deleteModalPost.style.display = "none";
	});

	confirmDeleteButtonPost.addEventListener("click", () => {
		posts.splice(postIdx, 1);
		localStorage.setItem("posts", JSON.stringify(posts));
		document.location.href = "Posts.html";
	});
});

// 댓글 HTML 렌더링
const renderComments = (comments) => {
	return comments
		.map((comment, index) => {
			const isAuthor =
				comment.author ===
				JSON.parse(sessionStorage.getItem("loginUser")).email;
			const buttonToAdd = isAuthor
				? `<div class="buttons">
                <button class="fixButton" data-index="${index}">수정</button> 
                <button class="deleteButton" data-index="${index}">삭제</button> 
            </div>`
				: "";

			return `
            <div class="commentLists">
                <div class="commentsInfo">
                    ${comment.authorNickname} ${comment.date}
                    ${buttonToAdd}
                </div>
                <div class="commentContent">
                    ${comment.content}
                </div>
            </div>`;
		})
		.join("");
};
