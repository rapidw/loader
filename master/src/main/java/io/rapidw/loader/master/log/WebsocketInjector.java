package io.rapidw.loader.master.log;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebsocketInjector {

    private final SimpMessagingTemplate template;

    public WebsocketInjector(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        WebsocketAppender.setTemplate(template);
    }
}
