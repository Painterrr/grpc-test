# GRPC in Spring Boot Mini Project

## 1. 개요

간단한 3개의 프로젝트를 통해 Spring Boot 환경에서 gRPC를 사용해보자.

project: Client, Server, Interface

## 2. 설정

Spring Boot 버전: 3.1.10

JDK 버전: 17


## 3. 포스트맨을 통한 결과 확인

동작 중인 Client에게 원하는 인자를 쿼리스트링(name=world)으로 넣은 후, 요청을 보내면 아래와 같이gRPC 통신을 거쳐 응답값이 성공적으로 반환되는 것을 확인할 수 있다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/023a2af2-c77a-4921-aa81-e4c5aea43978/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/78eb96fe-3cfb-4484-8d60-521dad10bb4b/Untitled.png)

## 정리

1. 외부 request.
2. grpc client controller가 request 받고 grpc service에 전달.
3. grpc service는 yml(properties)에 등록한 hello-world를 인자로 받으며(@GrpcClient(”hello-world”)) gRPC Server로 통신하여 아래 메서드를 살짝 핥고 grpc service에 반환.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/a7a60832-4b5a-4d98-8fc1-b0b875a39ee8/Untitled.png)

4. service - controller로 response 반환

⇒ grpc를 통한 서버 간 통신 시도
