/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomMatrix;

import CustomMatrix.CustomMatrixState.BuildState;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.IOException;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
@Extension
public class CustomMatrixEnvironmentContributor {
    public void buildEnvironmentFor(Run r, EnvVars envs, TaskListener listener) throws IOException,
            InterruptedException {
    	
        BuildState bs = Util.getBuildStateFromRun(r);
        if( bs == null ) {
        	return;
        }

        if (bs.rebuildNumber > 0) {
            envs.put(Definitions.__REBUILD_VAR_NAME,bs.rebuildNumber + "");
        }
    }
}
