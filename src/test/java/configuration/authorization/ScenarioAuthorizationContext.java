package configuration.authorization;

import java.util.HashMap;
import java.util.Map;

public class ScenarioAuthorizationContext {
    private Map<AuthorizationContext, Object> contextStringMap = new HashMap<>();

    public ScenarioAuthorizationContext(){}

    public Object getContextValue(AuthorizationContext context) {
        return contextStringMap.get(context);
    }

    public void setContextValue(AuthorizationContext authorizationContext, Object value){
        contextStringMap.put(authorizationContext, value);
    }
}
