package marko.gdv.alpha.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DatabaseConfig extends ProcessConfig {
    private String databaseType;
    private String databaseUrl;
    private String username;
    private String password;
}
