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
	<img src="https://img.shields.io/badge/Spring JPA-6DB33F?&logo=Spring JPA&logoColor=white">
  <br>
	<img src="https://img.shields.io/badge/JWT-0e093d?style=for-the-badge" />
	<img src="https://img.shields.io/badge/OAuth2-000000?style=for-the-badge" />
	<img src="https://img.shields.io/badge/SSE-160b7a?style=for-the-badge" />
 <br>
	<img src="https://img.shields.io/badge/Websocket-cc8812?style=for-the-badge" />
	<img src="https://img.shields.io/badge/STOMP-d10606?style=for-the-badge" />
	<img src="https://img.shields.io/badge/RabbitMQ-ff6a00?style=for-the-badge&logo=rabbitmq&logoColor=white" />
</div>
<br>

<h2 align=center>📚 Tools</h2>
<div align=center>
  <img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white" />
  <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white" />
 <img src="https://img.shields.io/badge/DBeaver-382923?style=for-the-badge&logo=DBeaver&logoColor=white" />
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
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
	<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
</div>
<br>

<h2 align=center>🧮 ERD</h2>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/115455126/cf5d62ad-cf62-4477-9869-032ad2f47fe8" />


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
<p>기록 상세 내용에 들어가서, 좋아요, 또는 댓글을 작성할 수 있다.</p>
<br>

<h3>운동 모집 API By Hyun Sik</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/4f1832a9-4a9d-4e05-bc8c-6394d1dbecc9"/>
<p>모집글 리스트 같은 경우 <b>검색어(제목), 지역, 운동 종목, 성별, 마감 여부</b>로 필터링 할 수 있다.</p>
<p>모집 글의 상태가 있는데 <b>모집 중</b>, <b>마감 임박</b>, <b>마감</b>으로 3가지로 나누어져 있다.</p>
<p>모집 글 상세 페이지는, 운동 모임에 대한 상세 정보가 표시가 된다.</p>
<p>- 위치, 운동 실력, 평균 운동 실력.</p>
<p>- 타유저 모임 참여 신청.</p>
<p>- 위치 같은 경우 Daum 주소 API를 통해 도로명 주소를 검색하고, Kakao Map API를 이용하여 지도에 위치를 보여준다.</p>
<p>운동 모집 글 지원자 관리에서, 모임에 신청한 유저들 중, 어떤 유저를 수락할지, 몇 명이 수락되었는지 확인할 수 있다.</p>
<p>- 해당 글을 통해서, 운동 모임 그룹 채팅방을 만들 수 있다.</p>
<br>

<h3>알림 API By Hyun Sik</h3>
<p>SSE를 활용하여 알림 기능을 구현했다.</p>
<br>

<h3>마이 페이지 API By Hyun Sik</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/c5d3d861-c402-48a6-951f-19ccc49fc3ae"/>
<p>유저의 정보를 볼 수 있는 페이지다</p>
<p>유저의 관심 운동 종목, 매너 점수를 확인할 수 있고, 회원 정보와 운동 별 능력을 클릭하면 팝업으로 볼 수 있다.</p>
<p><b>평가하기</b></p>
<p>- 로그인한 유저는, 자신이 참여했던 모임에서, 같이 활동 했던 인원들을 평가할 수 있다.</p>
<p>- 모임 후 48시간 안에 최소 한 명을 평가해야 한다.</p>
<p><b>내 신청 현황</b></p>
<p>- 운동 모집에 신청한 현황을 볼 수 있다.</p>
<p><b>작성한 모집글 내역</b></p>
<p>- 로그인한 사람이 작성한 모집글 내역을 볼 수 있다.</p>
<p>- 관리 페이지를 통해, 운동 모집 글 지원자 관리 페이지로 이동한다.</p>
<p><b>내 정보 수정</b></p>
<p>- 유저의 정보를 수정할 수 있는 탭이다.</p>
<br>

<h3>평가 API By Hyun Sik</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/01acc250-4de4-4b86-8281-3dd848366ced" width=500px/>
<p><b>매너 점수</b>와 <b>운동 실력</b>을 평가하는 것이다.</p>
<p>- 매너 점수는 <b>친절, 열정, 팀워크 점수</b>로 구성되어 있고, 3개의 지표의 평균이 해당 유저의 <b>매너 점수</b>로 기록된다.</p>
<p>- 운동 실력은 10개로 나뉘고, 9개의 등급 중 하나로 평가할 수 있다. 한번도 평가를 받지 않았으면 <b>루키</b>로 저장된다.</p>
<p>- 운동 실력 지표 : 루키, 비기너 1~3, 아마추어 1~3, 세미프로 1~2, 프로</p>
<p>48시간 안에 모임을 했던 사람들 중, 최소 1명도 평가를 안 했으면 패널티가 부여된다. (1회당 매너 점수 총점에서 3점이 깎인다)</p>
<br>

<h3>프로필 API By Hyun Sik</h3>
<img src="https://github.com/AnonymousZB14/USports_BE/assets/108650920/c2b837f5-e913-4c97-9b8d-096509c46924"/>
<p>특정 유저의 <b>기록글과, 등록한 모집글</b>을 확인할 수 있다.</p>
<p>- 회원 정보를 조회하여, 해당 회원에 대한 상세 정보를 알 수 있다.</p>
<p>- 회원 정보를 알아야, 모임을 만들고, 신청을 받을 때, 모임 참여를 신청한 유저의 <b>신뢰도</b>를 판단할 수 있다.</p>
<p>- 해당 유저가 작성한 모든 기록글을 볼 수 있다.</p>
<p>- 해당 유저가 작성한 모든 모집글들을 볼 수 있다.</p>
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
<p>타 유저를 팔로우하는 기능이다.</p>
<p>비공개 계정일 경우, 수락을 해야지, 팔로우가 성사가 된다.</p>
<p>그 외에, 팔로우를 하면, 기록글에서, 내가 팔로우한 사람의 피드를 볼 수 있게 된다.</p>
<br>

<h3>신고/문의 API By Je Joon</h3>
<p>코드 상으로 해결하는 것이 어려운 것들을 해결하여, 어플리케이션에 대한 유저들의 신뢰도를 높이려는 기능이다.</p>
<p>유저들이 어드민에게 문의 또는 신고를 할 것들이 있을 때를 위해 만든 API다.</p>
<p>이 API를 통해 유저들은, 부적절한 내용을 발견하거나, 운동 모임에서 나쁜 일을 겪으면, 신고 또는 문의를 할 수 있다.</p>
<p>어드민은 모든 유저들의 신고/문의를 볼 수 있고, 해결 단계를 나눠서, 유저들이 어드민이 해당 신고/문의를 해결하고 있는지 확인할 수 있도록 만들었다.</p>
<p><b>Registered</b> 상태 : 유저가 신고/문의를 등록 함. (아직 어드민은 보지 못 한 상태)</p>
<p><b>ING</b> 상태 : 어드민이 유저의 신고/문의를 해결하는 중이다.</p>
<p><b>FINISHED</b> 상태 : 어드민이 신고/문의를 해결했을 때.</p>
<br>

