package io.rapidw.loader.master.request;

import lombok.Data;

import java.util.List;

@Data
public class SupervisorRemoveRequest {

    private List<Integer> ids;
}
