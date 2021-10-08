# PersonalVoiceChannelBot
디스코드 개인 통화방 및 채팅방 생성 봇

# 제작자
[황선우(hwangseonu)](https://github.com/hwangseonu), [앨빈(alvin0319)](https://github.com/alvin0319)

# 사용법

#### Step 1: 프로젝트 필드

**윈도우**는 `.\gradlew clean shadowJar`, **리눅스 계열**은 `./gradlew clean shadowJar`을 이용해 봇을 빌드해주세요.

#### Step 2(옵션): Postgresql 설치 / 구성 (만약 설치되어있다면 Step 3으로 이동해주세요)

[링크](https://www.postgresql.org)를 클릭해 Postgresql을 설치할 수 있습니다.

관리자 권한으로 Postgresql에 로그인 한 후, 다음 명령어를 통해 사용자를 생성해주세요.

```postgresql
create user user_name with encrypted password 'mypassword';
```

`user_name`과 `mypassword`는 자신이 원하는대로 수정해주세요. (필수)

사용자를 생성한 다음, 다음 명령어를 통해 데이터베이스를 생성해주세요.

```postgresql
create database db_name;
```

`db_name`은 자신의 데이터베이스로 수정해주세요. (필수)

데이터베이스를 생성한 다음, 다음 명령어를 통해 데이터베이스에 권한을 부여해주세요.

```postgresql
grant all privileges on database db_name to user_name;
```

`db_name`에는 데이터베이스 생성때 사용한 이름을, `user_name`에는 사용자 생성때 사용한 이름을 입력해주세요.

#### Step 3: 봇 설정
먼저 봇을 실행시켜 줍니다: `java -jar PersonalVoiceChannelBot-1.0-SNAPSHOT.jar`

그 다음 봇을 정지시킨 다음, `config.json` 파일에 들어가서 디스코드 봇의 `토큰`과 `Postgresql` 서버를 설정해주세요.

#### Step 4: 봇 실행

명령어: `java -Xms1G -Xmx2G -jar PersonalVoiceChannelBot-1.0-SNAPSHOT.jar`

권장 메모리 옵션은 최소 `1G`입니다.

`-Xms` 옵션과 `-Xmx` 옵션을 수정해 최대 / 최소 메모리를 수정할 수 있습니다.