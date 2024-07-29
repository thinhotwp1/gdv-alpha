package marko.gdv.alpha.process;

import java.util.Map;

public abstract class AbstractProcessGdv<I, O> implements ProcessGdv<I, O> {
    protected Map<String, String> config;

    public AbstractProcessGdv(Map<String, String> config) {
        this.config = config;
    }
}
