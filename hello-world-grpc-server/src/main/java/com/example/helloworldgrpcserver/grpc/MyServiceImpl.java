package com.example.helloworldgrpcserver.grpc;

import com.example.helloworldgrpcinterface.lib.HelloRequest;
import com.example.helloworldgrpcinterface.lib.HelloResponse;
import com.example.helloworldgrpcinterface.lib.MyServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

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
