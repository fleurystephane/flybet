package configuration.projects;

import configuration.pronos.PronosContext;

import java.util.HashMap;
import java.util.Map;

public class ScenarioProjectContext {
    private Map<ProjectContext, Object> contextStringMap = new HashMap<>();

    public ScenarioProjectContext(){}

    public Object getContextValue(ProjectContext context) {
        return contextStringMap.get(context);
    }

    public void setContextValue(ProjectContext projectContext, Object value){
        contextStringMap.put(projectContext, value);
    }
}
