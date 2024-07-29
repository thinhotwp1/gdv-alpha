package marko.gdv.alpha.process;

import java.util.Map;

public class ProcessGetFileAzure extends AbstractProcessGdv<String, String> {

    public ProcessGetFileAzure(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Object input) {
        return input + " + getFileAzure";
    }
}
