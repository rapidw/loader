package io.rapidw.loader.master.entity;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import lombok.Builder;
import lombok.Getter;

import java.net.SocketAddress;

@Builder
@Getter
public class Supervisor {
    public enum Status {
        READY,
        RUNNING,
        ERRORED,
    }
    private SocketAddress address;
    private String path;
    private Status status;
    private StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver;

    public void loadAgent(byte[] data) {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setLoadReq(LoaderServiceOuterClass.LoadReq.newBuilder()
                .setData(ByteString.copyFrom(data))
                .build())
            .build()
        );
    }

    public void configAgent(byte[] agentParamBytes, byte[] agentConfigBytes) {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setAgentConfigReq(LoaderServiceOuterClass.AgentConfigReq.newBuilder()
                .setAgentParamsBytes(ByteString.copyFrom(agentParamBytes))
                .setAgentConfigBytes(ByteString.copyFrom(agentConfigBytes))
                .build())
            .build()
        );
    }

    public void configSupervisor(int rpsLimit, int perAgentTotalLimit, int durationLimit) {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setSupervisorConfigReq(LoaderServiceOuterClass.SupervisorConfigReq.newBuilder()
                .setRpsLimit(rpsLimit)
                .setPerAgentTotalLimit(perAgentTotalLimit)
                .setDurationLimit(durationLimit)
                .build())
            .build()
        );
    }

    public void startAgent() {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setStartReq(LoaderServiceOuterClass.StartReq.newBuilder()
                .build())
            .build()
        );
    }

    public void close() {
        responseObserver.onCompleted();
    }
}
