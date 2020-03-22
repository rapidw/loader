package io.rapidw.loader.master.entity;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.rapidw.loader.common.gen.LoaderServiceOuterClass;
import lombok.Builder;
import lombok.Getter;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Builder
@Getter
public class Supervisor {

    private static AtomicInteger counter = new AtomicInteger();
    public enum Status {
        READY,
        RUNNING,
        ERRORED,
    }
    private int id;
    private SocketAddress address;
    private String path;
    private Status status;
    private StreamObserver<LoaderServiceOuterClass.MasterMessage> responseObserver;

    public static int nextId() {
        return counter.getAndIncrement();
    }

    public void loadAgent(byte[] data) {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setLoad(LoaderServiceOuterClass.Load.newBuilder()
                .setData(ByteString.copyFrom(data))
                .build())
            .build()
        );
    }

    public void configAgent(byte[] agentParamBytes, byte[] agentConfigBytes) {
        LoaderServiceOuterClass.AgentConfig.Builder builder = LoaderServiceOuterClass.AgentConfig.newBuilder();
        if (agentParamBytes != null) {
            builder.setAgentParamsBytes(ByteString.copyFrom(agentParamBytes));
        } else {
            builder.setAgentParamsBytes(ByteString.EMPTY);
        }
        if (agentConfigBytes != null) {
            builder.setAgentConfigBytes(ByteString.copyFrom(agentConfigBytes));
        } else {
            builder.setAgentConfigBytes(ByteString.EMPTY);
        }
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setAgentConfig(builder.build())
            .build()
        );
    }

    public void configSupervisor(int rpsLimit, int perAgentTotalLimit, int durationLimit) {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setSupervisorConfig(LoaderServiceOuterClass.SupervisorConfig.newBuilder()
                .setRpsLimit(rpsLimit)
                .setPerAgentTotalLimit(perAgentTotalLimit)
                .setDurationLimit(durationLimit)
                .build())
            .build()
        );
    }

    public void startAgent() {
        responseObserver.onNext(LoaderServiceOuterClass.MasterMessage.newBuilder()
            .setStart(LoaderServiceOuterClass.Start.newBuilder()
                .build())
            .build()
        );
    }

    public void close() {
        responseObserver.onCompleted();
    }
}
