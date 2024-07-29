package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessGdv1 extends AbstractProcessGdv<String, String> {

    public ProcessGdv1(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        return input + " ProcessGdv1";
    }
}
