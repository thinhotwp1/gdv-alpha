package marko.gdv.alpha.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public interface ProcessGdv extends Processor {
    @Override
    void process(Exchange exchange) throws Exception;
}
