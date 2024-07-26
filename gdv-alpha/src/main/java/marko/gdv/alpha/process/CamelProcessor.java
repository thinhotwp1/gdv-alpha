package marko.gdv.alpha.process;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultProducerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class CamelProcessor {

    private List<CamelContext> camelContexts = new ArrayList<>();
    private List<String> startEndpoints = new ArrayList<>();

    @Bean
    public void initializeCamelContexts() throws Exception {
        // Load configuration files
        File configDirectory = new File("src/main/resources/configs");
        File[] configFiles = configDirectory.listFiles((dir, name) -> name.endsWith(".properties"));

        if (configFiles != null) {
            for (File configFile : configFiles) {
                CamelContext context = createCamelContextFromConfig(configFile);
                camelContexts.add(context);
                context.start();
            }
        }
    }

    private CamelContext createCamelContextFromConfig(File configFile) throws Exception {
        CamelContext context = new DefaultCamelContext();

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }

        List<ProcessGdv> processList = new ArrayList<>();

        // Dynamically create process instances based on configuration
        int i = 1;
        while (true) {
            String classPath = properties.getProperty("process" + i + ".classpath");
            if (classPath == null) {
                break;
            }
            ProcessGdv process = (ProcessGdv) Class.forName(classPath).getConstructor().newInstance();
            processList.add(process);
            i++;
        }

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                String currentEndpoint = "direct:startProcess";
                for (int i = 0; i < processList.size(); i++) {
                    String nextEndpoint = "direct:process" + (i + 1);
                    from(currentEndpoint)
                            .process(processList.get(i))
                            .to(nextEndpoint);
                    currentEndpoint = nextEndpoint;
                }

                // Final process output to mock endpoint or other endpoint
                from(currentEndpoint)
                        .to("mock:result");

                // Remember the start endpoint
                startEndpoints.add("direct:startProcess");
            }
        });

        return context;
    }

    @Bean
    public ProducerTemplate producerTemplate(CamelContext camelContext) {
        return new DefaultProducerTemplate(camelContext);
    }

    @Scheduled(fixedRate = 10000)  // 10 seconds
    public void triggerCamelContexts() {
        for (String startEndpoint : startEndpoints) {
            for (CamelContext context : camelContexts) {
                context.createProducerTemplate().sendBody(startEndpoint, "Triggering process");
            }
        }
    }
}
