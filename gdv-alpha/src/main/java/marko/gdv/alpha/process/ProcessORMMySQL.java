package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessORMMySQL extends AbstractProcessGdv<String, String> {

    public ProcessORMMySQL(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        return input + " + mysql";
    }
}
