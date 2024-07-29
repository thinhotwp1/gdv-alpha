package marko.gdv.alpha.process.impl;

import marko.gdv.alpha.process.AbstractProcessGdv;

import java.util.Map;

public class ProcessGetFileAzure extends AbstractProcessGdv<String, String> {

    public ProcessGetFileAzure(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(String input) {
        // Logic get file in azure
        return input + " + getFileAzure";
    }
}
