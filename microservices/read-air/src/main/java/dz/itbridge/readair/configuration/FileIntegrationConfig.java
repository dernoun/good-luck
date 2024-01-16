package dz.itbridge.readair.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

import java.io.File;

@Configuration
@EnableIntegration
public class FileIntegrationConfig {

    private static final String INBOUND_PATH = "/Users/mohameddernoun/dev/AIR";

    @Bean
    public IntegrationFlow fileIntegrationFlow() {
        return IntegrationFlow.from(
                        Files.inboundAdapter(new File(INBOUND_PATH))
                                .autoCreateDirectory(true)
                                .preventDuplicates(true),
                        poller -> poller.poller(Pollers.fixedDelay(1000)))
                .transform(Files.toStringTransformer())
                .handle(message -> {
                    String content = (String) message.getPayload();
                    // Process the file content
                    System.out.println("Received file content: " + content);
                    // Publish a message to the message broker
//                    source.output().send(MessageBuilder.withPayload(content).build());
                })
                .get();
    }

    @Bean
    public IntegrationFlow fileReadingFlow() {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File(INBOUND_PATH))
                                .patternFilter("*.txt"),
                        e -> e.poller(Pollers.fixedDelay(1000)))
                .transform(Files.toStringTransformer())
                .channel("processFileChannel")
                .get();
    }
}
