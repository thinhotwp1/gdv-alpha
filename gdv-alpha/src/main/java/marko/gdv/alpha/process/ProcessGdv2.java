package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessGdv2 extends AbstractProcessGdv<String, String> {

    public ProcessGdv2(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        return input + " ProcessGdv2";
    }
}
