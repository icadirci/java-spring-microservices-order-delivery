package com.orderplatform.userservice.grpc;

import com.orderplatform.common.grpc.UserRequest;
import com.orderplatform.common.grpc.UserResponse;
import com.orderplatform.common.grpc.UserServiceGrpcNavGrpc.UserServiceGrpcNavImplBase;
import com.orderplatform.userservice.user.UserRepository;
import io.grpc.Status;
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

        userRepository.findById(userId).ifPresentOrElse(user -> {
            UserResponse response = UserResponse.newBuilder()
                    .setEmail(user.getEmail())
                    .setId(user.getId())
                    .setUsername(user.getFullName())
                    .setEnabled(user.isEnabled())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, () -> responseObserver.onError(
                Status.NOT_FOUND
                        .withDescription("User not found: " + userId)
                        .asRuntimeException()
        ));
    }
}
