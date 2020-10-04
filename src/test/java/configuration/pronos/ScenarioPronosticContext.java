package configuration.pronos;

import java.util.HashMap;
import java.util.Map;

public class ScenarioPronosticContext {
    private Map<PronosContext, Object> contextStringMap = new HashMap<>();

    public ScenarioPronosticContext(){}

    public Object getContextValue(PronosContext pronosContext) {
        return contextStringMap.get(pronosContext);
    }

    public void setContextValue(PronosContext pronosContext, Object value){
        contextStringMap.put(pronosContext, value);
    }
}
