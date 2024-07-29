package marko.gdv.alpha.process.impl;

import marko.gdv.alpha.entity.ContractDetail;
import marko.gdv.alpha.entity.Contracts;
import marko.gdv.alpha.process.AbstractProcessGdv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessGdv2Json extends AbstractProcessGdv<String, Contracts> {

    public ProcessGdv2Json(Map<String, String> config) {
        super(config);
    }

    @Override
    public Contracts start(String input) {
        // Logic with cast input to Contracts
        Contracts contracts = new Contracts();
        List<ContractDetail> list = new ArrayList<>();
        ContractDetail detail = new ContractDetail();
        list.add(detail);
        contracts.setContracts(list);
        return contracts;
    }
}
