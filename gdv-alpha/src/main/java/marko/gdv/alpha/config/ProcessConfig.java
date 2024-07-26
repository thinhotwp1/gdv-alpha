package marko.gdv.alpha.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessConfig {
    private String source;
    private String containerName;
    private String inputType;
    private String outputType;
    private String configPath;
    private String classPath;
}

