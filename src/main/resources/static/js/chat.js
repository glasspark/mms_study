$(document).ready(function() {
	// 팝업 창 열기 함수
	function openChatPopup() {
		const groupId = $('#groupId').val(); // Hidden field에서 groupId 가져오기
		const userNickname = $('#userNickname').val(); // Hidden field에서 groupId 가져오기
			console.log(userNickname);


		const popup = window.open(
			'', // URL (빈 값이면 내부 콘텐츠로)
			'chatPopup', // 창 이름
			'width=400,height=600,scrollbars=no,resizable=no'
		);

		// 팝업 창에 채팅 HTML 삽입
		popup.document.write(`
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Chat Room</title>
                <script src="https://cdn.jsdelivr.net/npm/sockjs-client/dist/sockjs.min.js"></script>
                <script src="https://cdn.jsdelivr.net/npm/stompjs/lib/stomp.min.js"></script>
                <script
			src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
          <link href="/css/styles.css" rel="stylesheet" />
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 10px;
                    }
                    #messages {
                        height: 400px;
                        overflow-y: scroll;
                        border: 1px solid #ccc;
                        padding: 10px;
                        margin-bottom: 10px;
                        list-style-type: none;
                    }
                    #messageInput {
                        width: 70%;
                    }
                    button {
                        padding: 5px 10px;
                    }
                </style>
            </head>
            <body>
                <div id="chat">
				   <div id="chat" class="card border-0 shadow rounded-4">
				    <div class="card-header bg-primary text-white">
				        <h2 class="card-title mb-0">Chat Room: <span id="roomIdDisplay">${groupId}</span></h2>
				    </div>
				    <div class="card-body">
				        <!-- 메시지 영역 -->
				        <ul id="messages" class="list-group mb-3" style="height: 300px; overflow-y: scroll;"></ul>
				        
				        <!-- 입력 필드 및 버튼 -->
				        <div class="input-group">
				            <input
				                type="text"
				                id="messageInput"
				                class="form-control"
				                placeholder="Type a message..."
				                aria-label="Type a message"
				            />
				            <button class="btn btn-primary" onclick="sendMessage()">Send</button>
				        </div>
				    </div>
				</div>
				
                </div>
                <script>
                    const roomId = "${groupId}"; // 전달받은 groupId를 roomId로 설정
                    const socket = new SockJS('/ws');
                    const stompClient = Stomp.over(socket);

                    stompClient.connect({}, function (frame) {
                        console.log("Connected: " + frame);
							/* 데이터 표시 부분 */
                        stompClient.subscribe('/topic/room/' + roomId, function (message) {
                            const chatMessage = JSON.parse(message.body);
                            const messages = document.getElementById("messages");
                            const newMessage = document.createElement("li");
                            newMessage.textContent = chatMessage.sender + ": " + chatMessage.content;
                            messages.appendChild(newMessage);
                        });
                    });

					/* 메세지 전송 부분 */
                    function sendMessage() {
                        const input = document.getElementById("messageInput");
                        const message = input.value;

                       stompClient.send('/api/chat/send-message', {}, JSON.stringify({
                            roomId: roomId,
              			    sender: '${userNickname}',
                            content: message,
                            type: "CHAT"
                        }));

                        input.value = '';
                    }
                </script>
            </body>
            </html>
        `);
	}

	// 클라이언트에서 사용자 참여 메시지 전송
	function joinRoom(roomId) {
		stompClient.send('/api/chat/addUser', {}, JSON.stringify({
			roomId: roomId,
			type: "JOIN"
		}));
	}


	// 버튼 클릭 이벤트에 팝업 창 열기 연결
	$('#openChatButton').on('click', function() {
		openChatPopup();
	});
});
