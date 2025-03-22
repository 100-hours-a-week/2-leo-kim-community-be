import { loadProfileMenu } from "./profileMenu.js";
import { getMe } from "./api/users.js";
import { getPostDetail, deletePost } from "./api/posts.js";
import { toggleLike } from "./api/likes.js";
import { createComment, updateComment, deleteComment } from "./api/comments.js";

document.addEventListener("DOMContentLoaded", async () => {
	// JWT 이상하면 로그인
	const myInfo = await getMe();
	if (myInfo.message.startsWith("JWT")) {
		document.location.href = "Log in.html";
	}

	// 데이터 가져오기
	const postId = localStorage.getItem("selectedPostIdx");
	const response = await getPostDetail(postId);
	const post = response.data;
	const commentsList = post.commentList;

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
	const header = document.getElementById("headerContents");
	const authorProfile = document.getElementById("authorProfile");

	let postLike = post.likes;
	let isLiked = post.isLiked;
	let selectedCommentId = null;

	// 프로필 사진 업데이트
	const profileImageUpdate = async () => {
		const profileImage = myInfo.data.profileImage;
		const profilePic = document.createElement("img");
		profilePic.id = "profilePic";
		profilePic.src = profileImage ? profileImage : "./profile_img.webp";
		profilePic.style.width = "30px";
		profilePic.style.height = "30px";
		profilePic.style.borderRadius = "50%";
		header.appendChild(profilePic);
	};

	// 프로필 사진 업데이트 후 프로필 메뉴 만드는 비동기처리
	profileImageUpdate().then(loadProfileMenu);

	// 내 포스트면 수정 삭제버튼 랜더링
	if (post?.isMyPost) {
		const buttonsSetup = () => {
			const modifyButton = document.getElementById("modifyBtn");
			modifyButton.addEventListener("click", () => {
				document.location.href = "edit post.html";
			});
		};

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
	// TODO : 프로필 이미지
	authorProfile.innerHTML = `<image src=${post.author.profileImage}>`;
	authorProfile.style.marginRight = "10px";
	title.innerText = post.title;
	author.innerText = post.author.nickname;
	date.innerText = post.regDate;
	contents.innerText = post.contents;
	if (post.image) {
		// TODO : 업로드 이미지
		picture.src = post.image;
	}

	// 게시글 정보 렌더링 (조회수, 좋아요, 댓글수)
	const renderPosts = () => {
		likes.innerHTML = `<div>${postLike}</div><div>좋아요 수</div>`;
		views.innerHTML = `<div>${post.views}</div><div>조회수</div>`;
		commentNums.innerHTML = `<div>${post.comments}</div><div>댓글</div>`;
	};

	// 좋아요 상태 렌더링
	const renderLikes = () => {
		likes.style.backgroundColor = isLiked ? "#ACA0EB" : "#D9D9D9";
	};

	// 댓글 HTML 렌더링
	const renderComments = (commentsList) => {
		comments.innerHTML = commentsList
			.map((comment) => {
				const buttonToAdd = comment.isMyComment
					? `<div class="buttons">
                <button class="fixButton" data-index="${comment.id}">수정</button> 
                <button class="deleteButton" data-index="${comment.id}">삭제</button> 
            </div>`
					: "";

				return `
            <div class="commentLists">
                <div class="commentsInfo">
                    ${comment.nickname} ${comment.regDate}
                    ${buttonToAdd}
                </div>
                <div class="commentContent">
                    ${comment.contents}
                </div>
            </div>`;
			})
			.join("");
	};

	// 초기 렌더링
	renderPosts();
	renderLikes();
	renderComments(commentsList);

	// 좋아요 버튼 클릭
	likes.addEventListener("click", async () => {
		await toggleLike(postId)
			.then(() => {
				postLike = isLiked ? postLike - 1 : postLike + 1;
				isLiked = !isLiked;
				renderPosts();
				renderLikes();
			})
			.catch((e) => {
				console.error(e);
			});
	});

	// 댓글 입력 시 버튼 색상 변경
	commentContent.addEventListener("input", () => {
		commentBtn.style.backgroundColor = commentContent.value
			? "#7F6AEE"
			: "#ACA0EB";
	});

	// 기본 댓글 추가 핸들러
	const setupDefaultCommentHandler = (button) => {
		button.addEventListener("click", async () => {
			if (commentContent.value) {
				await createComment(postId, commentContent.value);
				window.location.reload();
			}
		});
	};

	// 초기 댓글 핸들러 설정
	setupDefaultCommentHandler(commentBtn);

	// ------- 위에 까진 햇음 ------------

	// 댓글 수정
	const setupEditHandler = (commentId, initialContent) => {
		// textarea에 기존 댓글 내용 입력
		commentContent.value = initialContent;
		commentBtn.innerText = "댓글 수정";

		// 기존 버튼 이벤트 제거
		const newBtn = commentBtn.cloneNode(true);
		commentBtn.parentNode.replaceChild(newBtn, commentBtn);
		commentBtn = newBtn;

		// 수정 요청
		commentBtn.addEventListener("click", async () => {
			const newContent = commentContent.value;
			if (!newContent) return;

			try {
				await updateComment(commentId, newContent); // 🔧 API 요청
				window.location.reload(); // 또는 댓글만 다시 fetch해서 renderComments()
			} catch (e) {
				console.error("댓글 수정 실패:", e);
				alert("댓글 수정에 실패했습니다.");
			}
		});
	};

	//게시물 삭제
	const deleteButtonPost = document.getElementById("deleteBtn");
	const deleteModalComment = document.getElementById("deleteModalPost");
	const cancelButton = document.getElementById("cancelButtonPost");
	const confirmDeleteButton = document.getElementById(
		"confirmDeleteButtonPost"
	);

	deleteButtonPost.addEventListener("click", () => {
		deleteModalComment.style.display = "flex";

		confirmDeleteButton.addEventListener("click", async () => {
			await deletePost(postId);
			window.location.href = "Posts.html";
		});
	});

	// 댓글 삭제
	comments.addEventListener("click", (event) => {
		if (event.target.classList.contains("deleteButton")) {
			selectedCommentId = event.target.dataset.index;
			deleteModalComment.style.display = "flex";
		}

		if (event.target.classList.contains("fixButton")) {
			const commentId = event.target.dataset.index;
			const commentDiv = event.target.closest(".commentLists");
			const contentDiv = commentDiv.querySelector(".commentContent");
			const contentText = contentDiv.innerText;

			setupEditHandler(commentId, contentText);
		}
	});

	// 댓글 삭제 모달
	cancelButton.addEventListener("click", () => {
		deleteModalComment.style.display = "none";
		selectedCommentId = null;
	});

	confirmDeleteButton.addEventListener("click", async () => {
		if (!selectedCommentId) return;

		try {
			await deleteComment(selectedCommentId); // 🔧 API 요청
			window.location.reload(); // 또는 댓글만 다시 fetch해서 renderComments()
		} catch (e) {
			console.error("댓글 삭제 실패:", e);
			alert("댓글 삭제에 실패했습니다.");
		} finally {
			deleteModalComment.style.display = "none";
			selectedCommentId = null;
		}
	});
});
