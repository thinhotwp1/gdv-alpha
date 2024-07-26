package marko.gdv.alpha.process;

import marko.gdv.alpha.config.ProcessConfig;
import org.apache.camel.Exchange;

public class ProcessJson2Entity implements ProcessGdv {
    private ProcessConfig config;

    @Override
    public void process(Exchange exchange) throws Exception {
        String input = exchange.getIn().getBody(String.class);
        String output = transformJson(input, config);
        exchange.getIn().setBody(output);
    }

    private String transformJson(String input, ProcessConfig config) {
        // Implement your transformation logic here
        return "{}";
    }
}
