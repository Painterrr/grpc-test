# GRPC in Spring Boot Mini Project

## 1. 개요

간단한 3개의 프로젝트를 통해 Spring Boot 환경에서 gRPC를 사용해보자.

project: Client, Server, Interface

## 2. 설정

Spring Boot 버전: 3.1.10

JDK 버전: 17

## 3. 상세

본 프로젝트는 gRPC의 공식문서 중 Spring Boot 환경으로 진행하는 gRPC 프로젝트를 참고하여 진행한다.

참고: 

[gRPC-Spring-Boot-Starter Documentation](https://yidongnan.github.io/grpc-spring-boot-starter/en/)

해당 프로젝트의 목적은 Client에 Http 요청을 보냈을 때 응답으로 Hello —> ${name}이 돌아오도록 하는 것이다. ${name}은 RequestParameter로 사용자가 입력한 값이 들어가게 된다.

해당 프로젝트를 진행하기 위해서는 Client, Server, Interface 총 3개의 프로젝트를 생성해야 한다.

### 3-1. Client

Client는 사용자로부터 Http 요청을 받아 해당 요청을 처리한다.

큰 갈래는 기존의 Spring Boot 프로젝트와 크게 다르지 않지만, gRPC 사용 시 gRPC Client로 등록하여 gRPC Server로 gRPC 요청을 보내 응답을 받아오는 기능이 추가되었다.

### 3-2. Server

Server는 Client로부터 온 요청을 처리하는 로직을 구현한다.

해당 프로젝트는 Interface 서버에서 생성한 proto 서비스가 실질적으로 동작할 수 있도록 구현하는 역할을 한다.

### 3-3. Interface

Interface는 Spring Boot 프로젝트가 아닌 proto 파일을 작성하기 위한 Gradle 프로젝트다.

이후 다시 한 번 언급하겠지만, Interface는 Spring Boot 혹은 Java로 가능한 것이 아닌 proto 파일 작성만을 위한 프로젝트이기 때문에 Spring에 대한 의존성을 제외하고 만든다.

gRPC 프로토콜을 사용하는 데 있어 프로토콜을 통해 주고받을 요청과 응답, 그리고 실질적으로 동작할 로직을 proto 파일을 통해 추상적으로 구현하는 역할을 하기 때문에 주의 깊게 작성해야 한다. 

## 4. 프로젝트 설정

### 4-1. 최초 생성

우선 IntelliJ 혹은  https://start.spring.io/를 통해 총 3개의 Spring Boot 프로젝트를 생성한다. Build Tool로 Gradle을 추천한다. Maven을 선택해도 큰 문제는 없지만 본 예제가 Gradle로 진행되기 때문에 원활한 진행을 위해 Gradle을 추천한다.

Client 최초 생성 시에만 Spring Web 의존성을 추가하고 다른 프로젝트는 따로 의존성을 추가하지 않아도 된다. (문서와 동일한 이름)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/7b7c4d27-6801-4e21-99b9-8141ac41715e/Untitled.png)

### 4-2. Interface

프로젝트가 성공적으로 생성됐다면, gradle.build로 들어가 gRPC를 사용하기 위한 의존성을 추가해보자.

grpc-interface의 build.gradle 파일을 아래와 같이 설정한다.

```groovy
buildscript {
    ext {
        protobufVersion = '3.23.4'
        protobufPluginVersion = '0.8.18'
        grpcVersion = '1.58.0'
    }
}

plugins {
    id 'java-library'
    id 'com.google.protobuf' version "${protobufPluginVersion}"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation "io.grpc:grpc-protobuf:${grpcVersion}"
    implementation "io.grpc:grpc-stub:${grpcVersion}"
    compileOnly 'jakarta.annotation:jakarta.annotation-api:1.3.5' // Java 9+ compatibility - Do NOT update to 2.0.0
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    generatedFilesBaseDir = "$projectDir/src/generated"
    clean {
        delete generatedFilesBaseDir
    }
    plugins {
        grpc {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
    }
    generateProtoTasks {
        all()*.plugins {
            grpc {}
        }
    }
}
```

해당 파일을 잘 살펴보면 최초 생성 시 자동으로 생성된 Spring과 관련된 의존성과 Java와 관련된 설정들이 사라진 것을 확인할 수 있다.

이는 Interface의 역할이 gRPC 통신을 위한 proto 파일을 정의하기 위함이기 때문에, Spring과 Java와 관련된 불필요한 의존성을 제거한 것이라고 볼 수 있다.

build.gradle 파일을 위와 같이 변경하여 build 할 경우 Spring과 Java와 관련된 의존성이 사라졌기 때문에 @SpringBootApplication처럼 Spring과 관련된 코드에서 컴파일 에러가 발생한다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/0fa0cfaf-2049-42c1-9feb-074cbebc7b87/Untitled.png)

src/main 하위의 java 폴더와 해당 폴더의 하위 파일을 모두 삭제하도록 하자. 마찬가지로 src/test 폴더 또한 삭제하면 컴파일 에러 없이 정상적으로 build가 가능하다. (build 실행)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/ba22703d-ff1a-4fa3-8279-ab4a6d26356c/Untitled.png)

build가 성공적으로 진행됐다면, main 폴더 하위에 proto 폴더를 만들고 helloworld.proto 파일을 생성해보자.(proto 폴더가 기준 폴더 역할이라 생각됨)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/ac29c46b-6bef-4c0f-8fe2-8b637cd2d93c/Untitled.png)

생성한 helloworld.proto 파일에 아래와 같이 작성하자.

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "com.example.helloworldgrpcinterface.lib";
option java_outer_classname = "HelloWorldProto";

service MyService {
  rpc SayHello (HelloRequest) returns (HelloResponse) {
  }
}

message HelloRequest {
  string name = 1;
}

message HelloResponse {
  string message = 1;
}
```

proto 파일을 작성하는 방법에 대한 자세한 내용은 추후 작성할 예정이기 때문에 일단은 위의 내용과 동일하게 작성해보자.

proto 파일 작성 후, 다시 build를 진행하면 .build/libs 내부에 jar 파일이 생성된 것을 확인할 수 있으며, src 폴더 하위에 generated라는 이름의 폴더가 생성된 것 또한 확인할 수 있다. (안되면 gradle clean)

generated 폴더 내부를 확인해보면 helloworld.proto 파일에 작성한 내용들이 java 형식으로 변환된 것을 확인할 수 있다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/53218549-2764-4b45-8555-0cdc064767f7/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/d4933949-e46b-41ac-9496-62dc3faa68b0/Untitled.png)

### 4-3. Server

Server 프로젝트 또한 build.gradle을 아래와 같이 작성한다.

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.10'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE'
    implementation files('libs/HelloWorldGrpcInterface.jar')
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('bootBuildImage') {
    builder = 'paketobuildpacks/builder-jammy-base:latest'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

implementation 'net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE는 해당 프로젝트를 gRPC SERVER로 사용하겠다는 의미이다.

files('libs/HelloWorldGrpcInterface.jar') 해당 경로에 존재하는 jar 파일의 기능을 사용하겠다는 의미이다.

build 하기 전에 프로젝트 최상단에 libs 폴더를 생성하고, Interface의 jar 파일을 해당 폴더 내부에 넣는다. 이때, jar 파일의 이름을 HelloWorldGrpcInterface로 변경한다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/944de6ca-8088-42a5-91b8-7ed1dc46dfe6/Untitled.png)

jar 파일의 이름을 변경한 이유는 implementation files(’libs/HelloWorldGrpcInterface.jar’)와 포맷을 맞추기 위함이다. 만약 변경하고 싶지 않다면 위 의존성 부분의 jar 파일 이름을 변경하면 된다.

build.gradle을 위와 같이 변경했다면, build를 진행하자. build가 성공적으로 끝나면 Interface의 proto 파일에서 정의한 service를 구현해야 한다.

여기서부터는 일반적인 Spring Boot 프로젝트를 구현할 때와 매우 유사하다.

HelloWorldGrpcServerApplication 하위에 grpc라는 이름의 폴더를 만든 후, MyServiceImpl 클래스를 생성하고 아래와 같이 작성해보자.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/7e1f3b34-52de-4a10-8ef4-1fa1e1e72fe1/Untitled.png)

```java
@GrpcService
public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
            .setMessage("Hello ==> " + request.getName())
            .build()
        );

        responseObserver.onCompleted();
    }

}
```

MyServiceImpl의 코드를 살펴보고 각 부분이 의미하는 부분에 대해 살펴보자.

@GrpcService는 Spring Boot 애플리케이션이 이 클래스를 gRPC 서비스로 식별할 수 있도록 한다. MyServiceImpl 클래스가 gRPC 서비스임을 나타내는 어노테이션이라고 생각하면 된다.

responseObserver.onNext(…)는 클라이언트에게 보내는 응답을 의미한다. 여기서는 HelloResponse 객체에 message를 “Hello =⇒”와 요청으로부터 받은 name값을 message로 설정하여 클라이언트에게 응답한다는 의미다.

responseObserver.onCompleted()는 gRPC 통신이 성공적으로 완료되었다는 것을 의미한다.

MyServiceImpl의 작성이 완료되면 프로젝트별 포트가 중복되지 않도록 Server yml의 포트번호를 9090으로 변경하자.

```java
server:
	port: 9090
```

이후 해당 서버를 실행하면 아래와 같이 성공적으로 gRPCService가 등록되었다는 로그와 함께 9090 포트를 통해 앱이 실행되는 것을 확인할 수 있다.

```java
오후 4:44:24: Executing ':HelloWorldGrpcServerApplication.main()'...

> Task :compileJava
> Task :processResources
> Task :classes

> Task :HelloWorldGrpcServerApplication.main()

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.1.10)

2024-03-23T16:44:27.404+09:00  INFO 5944 --- [           main] c.e.h.HelloWorldGrpcServerApplication    : Starting HelloWorldGrpcServerApplication using Java 17.0.9 with PID 5944 (C:\grpc_test\hello-world-grpc-server\build\classes\java\main started by tttzk in C:\grpc_test)
2024-03-23T16:44:27.408+09:00  INFO 5944 --- [           main] c.e.h.HelloWorldGrpcServerApplication    : No active profile set, falling back to 1 default profile: "default"
2024-03-23T16:44:28.207+09:00  INFO 5944 --- [           main] g.s.a.GrpcServerFactoryAutoConfiguration : Detected grpc-netty-shaded: Creating ShadedNettyGrpcServerFactory
2024-03-23T16:44:28.608+09:00  INFO 5944 --- [           main] n.d.b.g.s.s.AbstractGrpcServerFactory    : Registered gRPC service: MyService, bean: myServiceImpl, class: com.example.helloworldgrpcserver.grpc.MyServiceImpl
2024-03-23T16:44:28.608+09:00  INFO 5944 --- [           main] n.d.b.g.s.s.AbstractGrpcServerFactory    : Registered gRPC service: grpc.health.v1.Health, bean: grpcHealthService, class: io.grpc.protobuf.services.HealthServiceImpl
2024-03-23T16:44:28.608+09:00  INFO 5944 --- [           main] n.d.b.g.s.s.AbstractGrpcServerFactory    : Registered gRPC service: grpc.reflection.v1alpha.ServerReflection, bean: protoReflectionService, class: io.grpc.protobuf.services.ProtoReflectionService
2024-03-23T16:44:28.779+09:00  INFO 5944 --- [           main] n.d.b.g.s.s.GrpcServerLifecycle          : gRPC Server started, listening on address: *, port: 9090
2024-03-23T16:44:28.791+09:00  INFO 5944 --- [           main] c.e.h.HelloWorldGrpcServerApplication    : Started HelloWorldGrpcServerApplication in 1.86 seconds (process running for 2.391)

```

### 4-4. Client

Client의 build.gradle은 아래와 같이 작성하면 된다.

```java
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.10'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    implementation 'net.devh:grpc-client-spring-boot-starter:2.15.0.RELEASE'
    implementation files('libs/HelloWorldGrpcInterface.jar')
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}

tasks.named('bootBuildImage') {
    builder = 'paketobuildpacks/builder-jammy-base:latest'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

implementation ‘net.devh:grpc-client-spring-boot-starter:2.15.0.RELEASE’에서 server가 client로 변경된 것, 롬복과 관련된 의존성이 추가된 것 이외에는 server의 build.gradle과 동일하다.

server와 마찬가지로 libs 폴더를 생성한 후 HelloWorldGrpcInterface.jar 파일을 위치시킨다. 이후 application.yml 파일에 아래와 같은 속성을 추가한다.

```yaml
server:
  port: 8081

grpc:
  client:
    hello-world:
      address: 'static://127.0.0.1:9090'
      negotiation-type: plaintext
```

- application.properties
    
    ```yaml
    spring.application.name=hello-world-grpc-client
    
    server.port = 8081
    
    grpc.client.hello-world.address=static://127.0.0.1:9090
    grpc.client.hello-world.negotiationType=plaintext
    ```
    

gRPC의 Client의 명칭을 hello-world로 설정하고 서버의 주소를 입력한 뒤, 통신방식을 plaintext로 설정한 것이다.

hello-world의 경우 원하는 명칭을 아무거나 사용해도 된다.

plaintext는 암호화되지 않은 평문으로 사용하겠다는 의미이며, 다른 옵션으로는 TLS(Transport Layer Security) 연결인 ‘TLS”가 있다.

여기까지 설정이 완료되면, 사용자의 요청을 받고 응답을 보낸 Controller와 gRPC와 통신할 Service를 만들어보자.

```java
@Service
public class GrpcClientService {

    @GrpcClient("hello-world")
    private MyServiceGrpc.MyServiceBlockingStub myServiceStub;

    public String sendMessage(final String name) {
        try {
            HelloResponse response = this.myServiceStub.sayHello(HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (StatusRuntimeException err) {
            return "Failed with " + err.getMessage();
        }
    }

}
```

```java
@RequiredArgsConstructor
@RestController
public class GrpcClientController {

    private final GrpcClientService grpcClientService;

    @GetMapping("/test")
    public String printMessage(@RequestParam String name) {
        return grpcClientService.sendMessage(name);
    }

}
```

Controller의 형태로는 기존의 Controller와 동일하기 때문에 Service에 대해서만 설명하자면, @GrpcClient(”hello-world”)는 yml에 등록했던 hello-world를 인자로 받으며 gRPC Server로 통신을 하는 Client라는 의미이다.

newBuilder()를 통해 HelloRequest 객체를 생성하여 myServiceStub을 통해 Server에서 구현한 메서드를 사용하면 해당 요청에 대한 결과가 HelloResponse 타입으로 변환된다.

## 5. 포스트맨을 통한 결과 확인

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/f7da080a-ed4c-4853-bd63-02d036fac83c/Untitled.png)

동작 중인 Client에게 원하는 인자를 쿼리스트링으로 놓은 후, 요청을 보내면 아래와 같이 성공적으로 gRPC 통신을 거쳐 응답값이 반환되는 것을 확인할 수 있다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/023a2af2-c77a-4921-aa81-e4c5aea43978/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/78eb96fe-3cfb-4484-8d60-521dad10bb4b/Untitled.png)

## 정리

1. 외부 request.
2. grpc client controller가 request 받고 grpc service에 전달.
3. grpc service는 yml(properties)에 등록한 hello-world를 인자로 받으며(@GrpcClient(”hello-world”)) gRPC Server로 통신하여 아래 메서드를 살짝 핥고 grpc service에 반환.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a55764c2-a0e7-42ed-bafc-d6334c8fcd32/a7a60832-4b5a-4d98-8fc1-b0b875a39ee8/Untitled.png)

1. 다시 service - controller로 반환

⇒ grpc를 통한 서버 간 통신 시도
