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
import java.util.logging.Logger;
import javax.servlet.ServletException;
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
    private MatrixProject project;
    private CustomMatrixState state;
    
    // indicates how should configurations be filtered
    private ConfigurationFilteringMethod confFilteringMethod;

    private static final Logger logger = Logger.getLogger(CustomMatrixAction.class.getName());
    private String combinationFilter;

    enum BuildType {
        MATRIXBUILD, MATRIXRUN, MATRIXPROJECT, UNKNOWN
    }
    
    enum ConfigurationFilteringMethod{
	MATRIX, COMBINATIONFILTER
    }

    public CustomMatrixAction(MatrixProject project) {
	if(project == null){
	    throw new IllegalArgumentException("Project cannot be null");
	}
	this.project = project;
	state  = CustomMatrixState.getInstance();
	state.addProject(project.toString(), project.getAxes());
	confFilteringMethod = ConfigurationFilteringMethod.MATRIX;
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
    
    public MatrixProject getProject(){
        return project;
    }

    public boolean isCombinationChecked(Combination c){
	return CustomMatrixState.getInstance().isCombinationChecked(project.toString(), c);
    }
    
    private void setAllCheckedFalse(){
	CustomMatrixState.getInstance().setAllCheckedFalse(project.toString());
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

    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws ServletException,
            IOException, InterruptedException {
        AbstractBuild<?, ?> build = req.findAncestorObject(AbstractBuild.class);

        BuildType type;
	if (build != null) {
            type = BuildType.MATRIXBUILD;
        } else if (req.findAncestor(MatrixProject.class) != null) {
            type = BuildType.MATRIXPROJECT;
        } else {
            type = BuildType.UNKNOWN;
        }
	
	
        List<ParameterValue> values = new ArrayList<ParameterValue>();
	
	/*
	 * Determine how configurations are filtered 
	 */
	confFilteringMethod = ConfigurationFilteringMethod.valueOf(req.getParameter("confFilter"));
	
	if(confFilteringMethod == ConfigurationFilteringMethod.COMBINATIONFILTER){
	    
	    combinationFilter = req.getParameter("combinationFilter");
	    state.addCombinationFilter(project.toString(), combinationFilter);
	    
	} else {
	    
	    /* Remove combination Filter, fields will be checked to false as 
	     * side effect
	     */
	    state.removeCombinationFilter(project.toString());

	    /* Generate the parameters */        
	    String input;
	    for(MatrixConfiguration conf : project.getActiveConfigurations()){
		Combination cb = conf.getCombination();
		input = req.getParameter(cb.toString());
		if(input != null){
		    state.setCombinationChecked(project.toString(), cb, true);
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
	
        /* Schedule the MatrixBuild */
        Hudson.getInstance().getQueue()
                .schedule(project, 0, new ParametersAction(values),
                        new CauseAction(new Cause.UserCause()));  
	
        /*
         * Depending on where the form was submitted, the number of levels to
         * direct
         */
        if (type.equals(BuildType.MATRIXBUILD)) {
            rsp.sendRedirect("../../");
        } else {
            rsp.sendRedirect("../");
        }
    }

    public boolean getMatrixChecked(){
	if(confFilteringMethod == ConfigurationFilteringMethod.MATRIX){
	    return true;
	}
	return false;
    }
    
    public boolean getCombinationFilterChecked(){
	if(confFilteringMethod == ConfigurationFilteringMethod.COMBINATIONFILTER){
	    return true;
	}
	return false;
    }
    
    public String getCombinationFilter(){
	return combinationFilter;
    }
    
    public String getAxes(){
	return project.getAxes().toString();
    }
    
}
