package marko.gdv.alpha.process;

import marko.gdv.alpha.config.DatabaseConfig;
import marko.gdv.alpha.config.ProcessConfig;
import marko.gdv.alpha.entity.Contracts;
import org.apache.camel.Exchange;

public class ProcessPushEntity implements ProcessGdv {
    private DatabaseConfig config;

    @Override
    public void process(Exchange exchange) throws Exception {
        String input = exchange.getIn().getBody(String.class);
        Contracts entity = mapJsonToEntity(input);
        pushToDatabase(entity, config);
    }

    private Contracts mapJsonToEntity(String json) {
        // Implement your mapping logic here
        return new Contracts();
    }

    private void pushToDatabase(Contracts entity, DatabaseConfig config) {
        // Implement your database logic here based on config.databaseType
    }
}
