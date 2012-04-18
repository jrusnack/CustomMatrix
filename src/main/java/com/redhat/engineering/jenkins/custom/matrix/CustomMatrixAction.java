/*
 *  The MIT License
 *
 *  Copyright 2012 Red Hat, Inc.
 *  Copyright 2011 Praqma A/S.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package com.redhat.engineering.jenkins.custom.matrix;

import hudson.matrix.*;
import hudson.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jfree.util.Log;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * The Custom Matrix Action class. This enables the plugin to add the link
 * action to the side panel.
 * 
 * @author wolfgang
 */
public class CustomMatrixAction implements Action {
    private String project;
    private CustomMatrixState state;

    private static final Logger logger = Logger.getLogger(CustomMatrixAction.class.getName());

    enum BuildType {
        MATRIXBUILD, MATRIXRUN, MATRIXPROJECT, UNKNOWN
    }

    public CustomMatrixAction(MatrixProject project) {
	this.project = project.toString();
	state  = CustomMatrixState.getInstance();
	state.addProject(project.toString(), project.getAxes());
    } 


    public String getDisplayName() {
        return Definitions.__DISPLAY_NAME;
    }

    public String getIconFileName() {
        return Definitions.__ICON_FILE_NAME;
    }

    public String getUrlName() {
        return Definitions.__URL_NAME;
    }

    public String getPrefix() {
        return Definitions.__PREFIX;
    }

    public boolean isCombinationChecked(Combination c){
	return CustomMatrixState.getInstance().isCombinationChecked(project, c.toString());
    }
    
    private void setAllCheckedFalse(){
	CustomMatrixState.getInstance().setAllCheckedFalse(project);
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
	    mp  = ((MatrixRun)build).getParentBuild().getProject();
	} else {
	    Log.warn("Unable to determine matrix project");
	    return false;
	}

	return combinationExists(mp,c);
    }
    
    /**
     * Adds configuration to the build state of build and schedules new build. 
     * Behavior depends on context where was Custom Matrix run from:
     * <ul>
     * <li>if parameter build is null, then run from project menu context is 
     * assumed</li> 
     * <li>if parameter build is not null, then run from build context is assumed
     * and build parameters are added, parameter project is not required</li> 
     * </ul>
     * 
     * @param project	required if build is null
     * @param build	required if project is null
     * @param formData	
     * 
     */
    public void performConfig(AbstractProject<?, ?> project, 
	    AbstractBuild<?, ?> build, Map<String, String[]> formData) {
	
	String uuid;
        List<ParameterValue> values = new ArrayList<ParameterValue>();

        logger.info("[CMP] The Custom Matrix Form has been submitted");

	if(project == null){
	    project = build.getProject();
	}
	
        /* UUID */	
//	if(build != null){
//	    uuid = project.getDisplayName() + "_" + build.getNumber() + "_"
//                + System.currentTimeMillis();
//	} else {
//	    uuid = project.getDisplayName() + "_" + project.getNextBuildNumber() + "_"
//                + System.currentTimeMillis();	
//	}
	

//        logger.fine("UUID given: " + uuid);

	state.setAllCheckedFalse(project.toString());
        /* Generate the parameters */
        Set<String> keys = formData.keySet();
        for( String key : keys ) {
        	
            /* Check the fields of the form */
            if (key.startsWith(Definitions.__PREFIX)) {
                String[] vs = key.split(Definitions.__DELIMITER, 2);
                try {
                    if (vs.length > 1) {
                    	logger.info("[CMP] adding " + key );
			state.setCombinationChecked(project.toString(), vs[1], true);
                    }

                } catch (JSONException e) {
                    /* No-op, not the field we were looking for. */
                }
            }
        }
	
	if(build != null){
	    /* Get the parameters of the build, if any and add them to the build */
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
        //values.add(new StringParameterValue(Definitions.__UUID, uuid));

        /* Schedule the MatrixBuild */
        Hudson.getInstance().getQueue()
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
	} else if (type.equals(BuildType.MATRIXBUILD)) {
            rsp.sendRedirect("../../");
        } else {
            rsp.sendRedirect("../");
        }
    }

}
