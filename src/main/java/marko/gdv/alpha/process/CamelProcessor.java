package marko.gdv.alpha.process;

import jakarta.annotation.PostConstruct;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class CamelProcessor {

    private final Map<File, ScheduledExecutorService> scheduledServices = new HashMap<>();
    private final Map<File, CamelContext> camelContexts = new HashMap<>();

    @PostConstruct
    public void initializeCamelContexts() throws Exception {
        File configDirectory = new File("configs");
        File[] configFiles = configDirectory.listFiles((dir, name) -> name.endsWith(".properties"));

        if (configFiles != null) {
            for (File configFile : configFiles) {
                runCamelContext(configFile);
            }
        }

        // Start file watching in a separate thread (NIO 2.0)
        new Thread(this::watchConfigDirectory).start();
    }

    private <I, O> CamelContext createCamelContextFromConfig(File configFile) throws Exception {
        CamelContext context = new DefaultCamelContext();

        Map<Integer, ProcessGdv<I, O>> processMap = new TreeMap<>();
        loadProperties(configFile, processMap);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                String currentEndpoint = "direct:startProcess";
                for (Map.Entry<Integer, ProcessGdv<I, O>> entry : processMap.entrySet()) {
                    String nextEndpoint = "direct:process" + entry.getKey();
                    from(currentEndpoint)
                            .process(exchange -> {
                                long startTime = System.currentTimeMillis();

                                I input = (I) exchange.getIn().getBody();
                                exchange.getIn().setHeader("inputBody", input);
                                O output = entry.getValue().start(input);
                                exchange.getIn().setBody(output);

                                long elapsedTime = System.currentTimeMillis() - startTime;
                                exchange.getIn().setHeader("processingTime", elapsedTime);
                            })
                            .log(LoggingLevel.INFO, "================================")
                            .log(LoggingLevel.INFO, "Process ID: " + entry.getKey())
                            .log(LoggingLevel.INFO, "Input Body: ${header.inputBody}")
                            .log(LoggingLevel.INFO, "Output: ${body}")
                            .log(LoggingLevel.INFO, "Processing time: ${header.processingTime} ms")
                            .to(nextEndpoint);
                    currentEndpoint = nextEndpoint;
                }

                from(currentEndpoint)
                        .log(LoggingLevel.INFO, "Final output: ${body}")
                        .to("mock:result");
            }
        });

        return context;
    }

    private static <I, O> void loadProperties(File configFile, Map<Integer, ProcessGdv<I, O>> processMap) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }

        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith(".classpath")) {
                int processNumber = Integer.parseInt(key.replace("process", "").replace(".classpath", ""));
                String classPath = properties.getProperty(key);
                String configFilePath = properties.getProperty("process" + processNumber + ".configFile");

                Properties processProperties = new Properties();
                try (FileInputStream configFis = new FileInputStream(new File("configs/process/" + configFilePath))) {
                    processProperties.load(configFis);
                }

                ProcessGdv<I, O> process = (ProcessGdv<I, O>) Class.forName(classPath).getConstructor(Map.class).newInstance(processProperties);
                processMap.put(processNumber, process);
            }
        }
    }

    private int getScheduleInterval(File configFile) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        }
        return Integer.parseInt(properties.getProperty("time.schedule", "30000")); // default to 30 seconds if not found
    }

    private void runCamelContext(CamelContext context) {
        try {
            context.createProducerTemplate().sendBody("direct:startProcess", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watchConfigDirectory() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Paths.get("configs").register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    if (fileName.toString().endsWith(".properties")) {
                        File updatedFile = new File("configs/" + fileName);
                        reloadConfigFile(updatedFile);
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void reloadConfigFile(File configFile) {
        try {
            // Stop the existing context and scheduler
            CamelContext context = camelContexts.get(configFile);
            if (context != null) {
                context.stop();
                scheduledServices.get(configFile).shutdownNow();
                scheduledServices.remove(configFile);
                camelContexts.remove(configFile);
            }

            // Reinitialize and start the new context
            runCamelContext(configFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runCamelContext(File configFile) throws Exception {
        CamelContext newContext = createCamelContextFromConfig(configFile);
        camelContexts.put(configFile, newContext);
        newContext.start();

        int interval = getScheduleInterval(configFile);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduledServices.put(configFile, scheduler);
        scheduler.scheduleAtFixedRate(() -> runCamelContext(newContext), 0, interval, TimeUnit.MILLISECONDS);
    }
}
