package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessGdv2Json extends AbstractProcessGdv<String, String> {

    public ProcessGdv2Json(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        return input + " + gdv2json";
    }
}
