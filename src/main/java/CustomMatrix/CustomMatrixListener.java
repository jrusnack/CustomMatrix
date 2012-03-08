/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomMatrix;

import CustomMatrix.CustomMatrixState.BuildState;
import hudson.Extension;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
@Extension
public class CustomMatrixListener extends RunListener<Run> {
    
     public CustomMatrixListener() {
        super(Run.class);
    }

    @Override
    public void onStarted(Run run, TaskListener listener) {
        if (run instanceof MatrixBuild) {
            /**/
            BuildState bs = Util.getBuildStateFromRun(run);
            if( bs == null ) {
            	return;
            }
            
            MatrixBuild mb = (MatrixBuild)run;
            MatrixBuild base = mb.getProject().getBuildByNumber(bs.rebuildNumber);
            
        }
    }

    /**
     * Add the Matrix Reloaded link to the build context
     */
    @Override
    public void onCompleted(Run run, TaskListener listener) {
        /* Test for MatrixBuild and add to context */
        if (run instanceof MatrixBuild) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>)run;

            CustomMatrixAction action = new CustomMatrixAction();
            build.getActions().add(action);
        }

        /* Test for MatrixRun and add to context */
        if (run instanceof MatrixRun) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>)run;

            CustomMatrixAction action = new CustomMatrixAction(((MatrixRun)run).getParent().getCombination().toString());
            build.getActions().add(action);
        }
    }
}
