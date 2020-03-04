package io.rapidw.loader.master.log;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebsocketAppender extends AppenderBase<ILoggingEvent> {

    @Setter
    private static SimpMessagingTemplate template = null;
    @Getter
    @Setter
    private PatternLayoutEncoder encoder;

    @Override
    public void start() {
        if (isStarted())
            return;
        super.start();
    }

    @Override
    public void stop() {
        if (!isStarted())
            return;
    }

    @Override
    protected void append(ILoggingEvent event) {

        if (event == null)
            return;
        byte[] bytes = encoder.encode(event);
        if (template != null) {
            template.convertAndSend("/logs", new String(bytes));
        }
    }
}
