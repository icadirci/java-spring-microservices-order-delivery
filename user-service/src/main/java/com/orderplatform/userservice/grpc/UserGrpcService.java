package com.orderplatform.userservice.grpc;

import com.orderplatform.common.grpc.UserRequest;
import com.orderplatform.common.grpc.UserResponse;
import com.orderplatform.common.grpc.UserServiceGrpcNavGrpc.UserServiceGrpcNavImplBase;
import com.orderplatform.userservice.user.UserRepository;
import com.orderplatform.userservice.user.entity.User;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpcNavImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver){
        long userId = request.getId();

        User user = userRepository.getReferenceById(userId);

        if (user.isEnabled()){
            UserResponse response = UserResponse.newBuilder()
                    .setEmail(user.getEmail())
                    .setId(userId)
                    .setUsername(user.getFullName())
                    .setEnabled(true)
                    .build();

            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }
}
