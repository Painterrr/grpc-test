package com.example.helloworldgrpcclient.service;

import com.example.helloworldgrpcinterface.lib.HelloRequest;
import com.example.helloworldgrpcinterface.lib.HelloResponse;
import com.example.helloworldgrpcinterface.lib.MyServiceGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

// import net.devh.boot.grpc.server.service.GrpcService;

@Service
public class GrpcClientService {

    @GrpcClient("hello-world")
    private MyServiceGrpc.MyServiceBlockingStub myServiceBlockingStub;

    public String sendMessage(final String name) {
        try {
            HelloResponse response = this.myServiceBlockingStub.sayHello(HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (StatusRuntimeException err) {
            return "Fail with" + err.getMessage();
        }
    }

}
