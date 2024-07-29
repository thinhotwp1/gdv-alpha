package marko.gdv.alpha.process.impl;

import marko.gdv.alpha.entity.Contracts;
import marko.gdv.alpha.process.AbstractProcessGdv;

import java.util.Map;

public class ProcessORMPostgres extends AbstractProcessGdv<Contracts, String> {

    public ProcessORMPostgres(Map<String, String> config) {
        super(config);
    }

    @Override
    public String start(Contracts contracts) {
        // Logic save contracts to postgres
        return " Save postgres: " + contracts.getContracts().size() + " contracts";
    }
}
