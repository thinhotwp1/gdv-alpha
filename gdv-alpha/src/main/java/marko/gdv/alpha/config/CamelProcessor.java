package marko.gdv.alpha.config;

import jakarta.annotation.PostConstruct;
import marko.gdv.alpha.process.ProcessGdv;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

@Component
public class CamelProcessor {

    private final List<CamelContext> camelContexts = new ArrayList<>();

    @PostConstruct
    public void initializeCamelContexts() throws Exception {
        // Load configuration files
        File configDirectory = new File("configs");
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

        Map<Integer, ProcessGdv<?, ?>> processMap = new TreeMap<>();
        loadProperties(configFile, processMap);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                String currentEndpoint = "direct:startProcess";
                for (Map.Entry<Integer, ProcessGdv<?, ?>> entry : processMap.entrySet()) {
                    String nextEndpoint = "direct:process" + entry.getKey();
                    from(currentEndpoint)
                            .process(exchange -> {
                                Object input = exchange.getIn().getBody(Object.class);
                                Object output = entry.getValue().start(input);
                                exchange.getIn().setBody(output);
                            })
                            .to(nextEndpoint);
                    currentEndpoint = nextEndpoint;
                }

                // Final process output to mock endpoint
                from(currentEndpoint)
                        .to("mock:result");
            }
        });

        return context;
    }

    private static void loadProperties(File configFile, Map<Integer, ProcessGdv<?, ?>> processMap) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }

        // Dynamically create process instances based on configuration
        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".classpath")) {
                int processNumber = Integer.parseInt(key.replace("process", "").replace(".classpath", ""));
                String classPath = properties.getProperty(key);
                String configFilePath = properties.getProperty("process" + processNumber + ".configFile");

                Properties processProperties = new Properties();
                try (FileInputStream configFis = new FileInputStream(new File("configs/process/" + configFilePath))) {
                    processProperties.load(configFis);
                }

                ProcessGdv process = (ProcessGdv) Class.forName(classPath).getConstructor(Map.class).newInstance(processProperties);
                processMap.put(processNumber, process);
            }
        }
    }

    @Scheduled(fixedRate = 10000)  // 10 seconds
    public void triggerCamelContexts() {
        for (CamelContext context : camelContexts) {
            context.createProducerTemplate().sendBody("direct:startProcess", "Start Process: ");
        }
    }
}
