<div align=center>
  <img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/8c530b03-ab59-4713-9a39-966586e58a46"/>
</div>


<h2 align=center>🧑‍💻 BackEnd Members</h2>


<div align=center>
	
| **김문진** | **류현식** | **이제준** |
| :------: |  :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/u/115455126?v=4" height=150 width=150> <br/> @KimMunjin](https://github.com/KimMunjin) | [<img src="https://avatars.githubusercontent.com/u/123939272?v=4" height=150 width=150> <br/> @HSRyuuu](https://github.com/HSRyuuu) | [<img src="https://avatars.githubusercontent.com/u/108650920?v=4" height=150 width=150> <br/> @jejoonlee](https://github.com/jejoonlee) |
| 기록 글, 팔로우 기능, 채팅, AWS S3 | 배포, 운동 모집, 유저 평가, 프로필, 마이 페이, 알림 | 회원가입, 로그인, 채팅, 신고/문의 |
	
</div>
<br>
<br>

<h2 align=center>📝 What is USPORTS</h2>
<div align=center>
  <p>내가 한 운동을 <b>기록</b>하고,</p>
  <p>함께 운동 <b>모임</b>을 만들어 운동을 함께 할 수 있는</p>
  <p><b>운동 SNS, 운동 모집 모임</b> 어플리케이션</p>
</div>
<br>

<h2 align=center>💻 Language & Framework</h2>
<div align=center>
	<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Conda-Forge&logoColor=white" />
	<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white" />
</div>

<h2 align=center>🛢 Database</h2>
<div align=center>
	<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white" />
	<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white" />
	<img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=MongoDB&logoColor=white" />
</div>
<br>

<h2 align=center>🎯 Skill Sets</h2>
<div align=center>
  <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white" />
	<img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white" />
  <br>
  <img src="https://img.shields.io/badge/JWT-0e093d?style=for-the-badge" />
  <img src="https://img.shields.io/badge/OAuth2-000000?style=for-the-badge" />
  <img src="https://img.shields.io/badge/SSE-160b7a?style=for-the-badge" />
  <br>
  <img src="https://img.shields.io/badge/Websocket-cc8812?style=for-the-badge" />
  <img src="https://img.shields.io/badge/STOMP-d10606?style=for-the-badge" />
  <img src="https://img.shields.io/badge/RabbitMQ-ff6a00?style=for-the-badge" />
</div>
<br>

<h2 align=center>📚 Tools</h2>
<div align=center>
  <img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white" />
 	<img src="https://img.shields.io/badge/DBeaver-382923?style=for-the-badge&logo=DBeaver&logoColor=white" />
  <br>
 	<img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white" />
  <img src="https://img.shields.io/badge/AmazoneEC2-FF9900?style=for-the-badge&logo=AmazoneEC2&logoColor=white" />
  <img src="https://img.shields.io/badge/AmazoneS3-569A31?style=for-the-badge&logo=AmazoneS3&logoColor=white" />
</div>
<br>

<h2 align=center>💭 Communication</h2>
<div align=center>
  <img src="https://img.shields.io/badge/Slack-e827f2?style=for-the-badge" />
  <img src="https://img.shields.io/badge/ZEP-a127f2?style=for-the-badge" />
</div>
<br>

<h2 align=center>🧮 ERD</h2>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/82e96fd4-db8d-4aa5-847c-0e1452592e65" />

<br>
<br>

<h2 align=center>📄 Features</h2>

<h3>회원 API By Je Joon</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/66efc2c6-1ca4-4a91-9b9b-9df329a65dd4" />
<p><b>OAuth2.0</b>를 활용하여 간편 로그인을 구현했습니다.</p>
<p>로그인을 할 때에 <b>JWT token</b>을 활용하여, <b>access token</b>과 <b>refresh token</b>을 유저에게 발급해줬습니다.</p>
<p><b>Redis</b>를 활용하여, <b>refresh token</b>을 저장하고, <b>cookie</b>에 <b>access token</b>을 저장했습니다.</p>
<p>로그아웃을 했을 때에는, <b>refresh token</b>을 <b>Redis</b>에서 삭제하고, <b>access token</b>을 블랙리스트로 <b>Redis</b>에 저장했습니다.</p>
<p>블랙리스트는, 로그아웃 후에, 같은 <b>access token</b>을 사용하려고 할 때에, <b>Redis</b>에 해당 <b>access token</b>이 저장되어 있으면, 해당 토큰을 사용하지 못 하게 막는 것입니다.</p>
<br>

<h3>기록 API By Mun Jin</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/a68222f4-4d56-457d-a61f-f10c61bcadf0"/>

<p>인스타그램 피드처럼 운동을 한 기록을 남기는 페이지다.</p>
<p>해당 페이지는 추천과 팔로잉, 두 페이지로 나누어져 있다.</p>
<p>추천 탭에서는 회원 가입 시 등록한 관심 종목에 대해 프로필 공개 상태인 회원들의 운동 기록들을 보여준다.</p>
<p>팔로잉 탭에서는 로그인한 유저가 팔로우한 회원들의 운동 기록들을 보여준다.</p>
<p>해당 기록은 운동 카테고리, 내용, 사진이 구성되어 있고, 사진은 최소 1장에서 최대 5장까지 업로드가 가능하며, AWS S3를 활용했다.</p>
<p>기록 상세 내용에 들어가서, 좋아요, 또는 댓글을 작성할 수 있다</p>
<br>

<h3>운동 모집 API By Hyun Sik</h3>
<br>

<h3>알림 API By Hyun Sik</h3>
<br>

<h3>마이 페이지 API By Hyun Sik</h3>
<br>

<h3>프로필 API By Hyun Sik</h3>
<br>

<h3>채팅 API By Je Joon & Mun Jin</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/16f8b617-1c68-481c-a406-f8bf10e6e02c"/>
<p><b>WebSocket</b>을 활용하여 실시간 채팅을 구현했습니다.</p>
<p><b>STOMP</b>를 사용하여, 채팅방을 구현했습니다.</p>
<p><b>STOMP</b>를 통해 구독과 발행 기능을 구현 하였고, 1대1 채팅과 운동 모임 그룹 채팅을 구현했습니다.</p>
<p>채팅방 같은 경우, 일반적인 <b>CRUD</b>를 활용했지만, 메세지를 보내고 받는 과정을 <b>STOMP</b>로 구현해야 했다.</p>
<p><b>STOMP</b>의 경우, 유저가 적으면 큰 문제가 없지만, 더 많은 데이터가 쌓일 경우를 대비하여 <b>RabbitMQ</b>를 사용했다.</p>
<p><b>RabbitMQ</b>는 외부 브로커이자 <b>Middleware</b>로, 중간에서 메세지 큐를 생성하여 메세지를 구독과 발행을 할 수 있도록 한 것이다.</p>
<p>외부 브로커를 사용하면, 어플리케이션 의존도를 느슨하게 만들어서, 서버 부담을 줄일 수 있다.</p>
<br>

<h3>팔로우 API By Mun Jin</h3>
<br>

<h3>신고/문의 API By Je Joon</h3>
<p>코드 상으로 해결하는 것이 어려운 것들을 해결하여, 어플리케이션에 대한 유저들의 신뢰도를 높이려는 기능이다.</p>
<p>유저들이 어드민에게 문의 또는 신고를 할 것들이 있을 때를 위해 만든 API다.</p>
<p>이 API를 통해 유저들은, 부적절한 내용을 발견하거나, 운동 모임에서 나쁜 일을 겪으면, 신고 또는 문의를 할 수 있다.</p>
<p>어드민은 모든 유저들의 신고/문의를 볼 수 있고, 해결 단계를 나눠서, 유저들이 어드민이 해당 신고/문의를 해결하고 있는지 확인할 수 있도록 만들었다.</p>
<p><b>Registered</b> 상태 : 유저가 신고/문의를 등록 함 (아직 어드민은 보지 못 한 상태)</p>
<p><b>ING</b> 상태 : 어드민이 유저의 신고/문의를 해결하는 중이다</p>
<p><b>FINISHED</b> 상태 : 어드민이 신고/문의를 해결했을 때</p>
<br>
