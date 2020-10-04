package configuration.account;

import configuration.projects.ProjectContext;

import java.util.HashMap;
import java.util.Map;

public class ScenarioAccountContext {
    private Map<AccountContext, Object> contextStringMap = new HashMap<>();

    public ScenarioAccountContext(){}

    public Object getContextValue(AccountContext context) {
        return contextStringMap.get(context);
    }

    public void setContextValue(AccountContext accountContext, Object value){
        contextStringMap.put(accountContext, value);
    }
}
