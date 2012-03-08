/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomMatrix;

import CustomMatrix.CustomMatrixState.BuildState;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import java.util.List;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class Util {
    public static BuildState getBuildStateFromRun( Run run ) {
		
        List<ParametersAction> actionList = run.getActions(ParametersAction.class);

        if (actionList.isEmpty()) {
            return null;
        }
        
        List<ParameterValue> pvs = actionList.get(0).getParameters();
        
        /* If the list is null */
        if( pvs == null ) {
        	return null;
        }
        
        StringParameterValue uuid = (StringParameterValue)getParameterValue(pvs, Definitions.__UUID);
        
        /* If the uuid is not defined, return true */
        if( uuid == null ) {
        	return null;
        }
        
        return CustomMatrixState.getInstance().getBuildState(uuid.value);
	}
	
    /**
     * Convenience method for retrieving {@link ParameterValue}s.
     * 
     * @param pvs A list of {@link ParameterValue}s.
     * @param key The key of the {@link ParameterValue}.
     * @return The parameter or null
     */
    public static ParameterValue getParameterValue(List<ParameterValue> pvs, String key) {
        for (ParameterValue pv : pvs) {
            if (pv.getName().equals(key)) {
                return pv;
            }
        }

        return null;
    }
}
