package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessORMPostgres extends AbstractProcessGdv<String, String> {

    public ProcessORMPostgres(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        return input + " + postgres";
    }
}
