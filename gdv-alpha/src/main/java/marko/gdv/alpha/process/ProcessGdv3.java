package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessGdv3 extends AbstractProcessGdv<String, String> {

    public ProcessGdv3(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        System.out.println(input + " ProcessGdv3");
        return input + " ProcessGdv3";
    }
}
