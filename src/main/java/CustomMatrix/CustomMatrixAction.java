/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomMatrix;

import CustomMatrix.CustomMatrixState.BuildState;
import hudson.matrix.*;
import hudson.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jfree.util.Log;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * The CustomMaxtrixAction class. Enables plugin to add the link
 * action to the side panel.
 * 
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class CustomMatrixAction implements Action {
    
    public String getDisplayName() {
        return Definitions.__DISPLAY_NAME;
    }

    public String getIconFileName() {
        return Definitions.__ICON_FILE_NAME;
    }

    public String getUrlName() {
        return Definitions.__URL_NAME;
    }
    private AbstractBuild<?, ?> build;

    private String checked = null;

    private static final Logger logger = Logger.getLogger(CustomMatrixAction.class.getName());

    enum BuildType {
        MATRIXPROJECT, MATRIXBUILD, MATRIXRUN, UNKNOWN
    }

    public CustomMatrixAction() {
    }

    public CustomMatrixAction(String checked) {
        this.checked = checked;
    }


    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public String getPrefix() {
        return Definitions.__PREFIX;
    }

    public String getChecked() {
        return this.checked;
    }

    public boolean combinationExists( MatrixProject mp, Combination c ){
	MatrixConfiguration mc = mp.getItem(c);
    	
    	/* Verify matrix configuration */
    	if( mc == null || !mc.isActiveConfiguration() ) {
    		return false;
    	} 
	
	return true;
    }
	
    public boolean combinationExists( AbstractBuild<?, ?> build, Combination c )
    {
    	MatrixProject mp = null;
    	
    	if(build instanceof MatrixBuild) {
    		mp = (MatrixProject) build.getProject();
    	} else if(build instanceof MatrixRun) {
    		mp = ((MatrixRun)build).getParentBuild().getProject();
    	} else {
    		Log.warn("Unable to determine matrix project");
    		return false;
    	}
    	
    	return combinationExists(mp,c);
    }

    //TODO: fix call from run
    public void performConfig(AbstractProject<?, ?> project,
	    AbstractBuild<?, ?>build, Map<String, String[]> formData) {
	
        List<ParameterValue> values = new ArrayList<ParameterValue>();

        logger.info("[MRP] The MATRIX RELOADED FORM has been submitted");
        //logger.info("[MRP]" + formData.toString(2));

	
        /* UUID */
        String uuid= project.getDisplayName() + "_" + (project.getLastBuild().number+1) + "_"
             + System.currentTimeMillis();
        if(build!=null){
            uuid = project.getDisplayName() + "_" + build.getNumber() + "_"
                + System.currentTimeMillis();
        }
        BuildState bs = CustomMatrixState.getInstance().getBuildState(uuid);

        logger.fine("UUID given: " + uuid);

        /* Generate the parameters */
        Set<String> keys = formData.keySet();
        for( String key : keys ) {
        	
            /*
             * The special form field, providing information about the build we
             * decent from
             */
            if (key.equals(Definitions.__PREFIX + "NUMBER")) {
                String value = formData.get(key)[0];
                try {
                    bs.rebuildNumber = Integer.parseInt(value);
                    logger.info("[MRP] Build number is " + bs.rebuildNumber );
                } catch (NumberFormatException w) {
                    /*
                     * If we can't parse the integer, the number is zero. This
                     * will either make the new run fail or rebuild it id
                     * rebuildIfMissing is set(not set actually)
                     */
                    bs.rebuildNumber = 0;
                }
                
                continue;
            }
        	
            /* Check the fields of the form */
            if (key.startsWith(Definitions.__PREFIX)) {
                String[] vs = key.split(Definitions.__DELIMITER, 2);
                try {
                    if (vs.length > 1) {
                    	logger.info("[MRP] adding " + key );
                    	bs.addConfiguration(Combination.fromString(vs[1]), true);
                    }

                } catch (JSONException e) {
                    /* No-op, not the field we were looking for. */
                }
            }


        }
	
        /* Get the parameters of the build, if any and add them to the build */
        if (build != null){
	    ParametersAction actions = build.getAction(ParametersAction.class);
	    if (actions != null) {
		List<ParameterValue> list = actions.getParameters();
		for (ParameterValue pv : list) {
		    // if( !pv.getName().startsWith( Definitions.prefix ) )
		    if (!pv.getName().equals(Definitions.__UUID)) {
			values.add(pv);
		    }
		}
	    }
	}

        /* Add the UUID to the new build. */
        values.add(new StringParameterValue(Definitions.__UUID, uuid));

        /* Schedule the MatrixBuild */
        Hudson.getInstance()
                .getQueue()
                .schedule(project, 0, new ParametersAction(values),
                        new CauseAction(new Cause.UserCause()));

    }

    
    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws ServletException,
            IOException, InterruptedException {
        AbstractBuild<?, ?> mbuild = req.findAncestorObject(AbstractBuild.class);
        AbstractBuild<?, ?> build = null;
	
	
        BuildType type;
	
	if (req.findAncestor(MatrixRun.class) != null) {
	    type = BuildType.MATRIXRUN;
	    build = ((MatrixRun)mbuild).getParentBuild();
	} else if (req.findAncestor(MatrixBuild.class) != null) {
            type = BuildType.MATRIXBUILD;
            build = mbuild;
        } else if (req.findAncestor(MatrixProject.class) != null) {
            type = BuildType.MATRIXPROJECT;
       //     MatrixProject pr =(MatrixProject) req.findAncestor(MatrixProject.class).getObject();
            
        } else {
            type = BuildType.UNKNOWN;
        }
	
	AbstractProject project = req.findAncestorObject(AbstractProject.class);

        JSONObject formData = req.getSubmittedForm();
        Map map = req.getParameterMap();
        Set<String> keys = map.keySet();
        System.out.println( "VALUES:" );
        for( String key : keys ) {
        	System.out.print( key + ": " );
        	for( String val : req.getParameterValues(key) ) {
        		System.out.print( val + "; " );
        	}
        	System.out.println( );
        }
        performConfig(project, build, map);

        /*
         * Depending on where the form was submitted, the number of levels to
         * direct
         */
	if (type.equals(BuildType.MATRIXRUN)) {
	    rsp.sendRedirect("../../../");
	}else if (type.equals(BuildType.MATRIXBUILD)) {
            rsp.sendRedirect("../../");
        } else {
            rsp.sendRedirect("../");
        }
    }

}
