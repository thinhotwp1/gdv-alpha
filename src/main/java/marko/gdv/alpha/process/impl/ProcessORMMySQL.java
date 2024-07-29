package marko.gdv.alpha.process.impl;

import marko.gdv.alpha.entity.Contracts;
import marko.gdv.alpha.process.AbstractProcessGdv;

import java.util.Map;

public class ProcessORMMySQL extends AbstractProcessGdv<Contracts, String> {

    public ProcessORMMySQL(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Contracts contracts) {
        return " Save mysql: " + contracts.getContracts().size() + " contracts";
    }
}
