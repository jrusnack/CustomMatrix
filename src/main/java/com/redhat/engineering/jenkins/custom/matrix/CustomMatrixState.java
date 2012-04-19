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

import hudson.matrix.AxisList;
import hudson.matrix.Combination;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to pass parameters from a form to a Run, given a uuid.
 * This is a singleton class.
 * 
 * @author wolfgang
 */
public class CustomMatrixState {
    
    /* Stores mapping project -> (combination -> boolean) */
    private Map<String,Map<Combination,Boolean>> projectCheckedCombinations;
    
    /* Stores mapping project -> combinationFilter */
    private Map<String, String> projectCombinationFilter;
    
    private Map<String, AxisList> projectAxisList;
    
    private static final CustomMatrixState instance = new CustomMatrixState();

    private CustomMatrixState() {
	projectCheckedCombinations = new HashMap<String, Map<Combination, Boolean>>();
	projectCombinationFilter = new HashMap<String, String>();
	projectAxisList = new HashMap<String, AxisList>();
    }

    public static CustomMatrixState getInstance() {
        return instance;
    }

    /**
     * Returns whether to build certain combination of given project or not. If
     * combination or project was not set prior to the call, returns true.
     * 
     * @param project	    
     * @param combination   
     * @return		    True if should be built, false otherwise
     */
    public boolean isCombinationChecked(String project, Combination combination){
	if(projectCheckedCombinations.containsKey(project) && projectCheckedCombinations.get(project).containsKey(combination)){
	    return projectCheckedCombinations.get(project).get(combination);
	}	    
	return true;
    }

    /**
     * Sets all combinations for the project to false (do not build).
     * 
     * @param project Project for which to set all combinations to false
     */
    public void setAllCheckedFalse(String project){
	if(projectCheckedCombinations.containsKey(project)){
	    for(Combination combination : projectCheckedCombinations.get(project).keySet()){
		projectCheckedCombinations.get(project).put(combination, false);
	    }
	}
    }
    
    /**
     * Sets all combinations for the project to true (do build).
     * 
     * @param project Project for which to set all combinations to true
     */
    public void setAllCheckedTrue(String project){
	if(projectCheckedCombinations.containsKey(project)){
	    for(Combination combination : projectCheckedCombinations.get(project).keySet()){
		projectCheckedCombinations.get(project).put(combination, true);
	    }
	}
    }
    
    /**
     * Set combination for project to be checked/unchecked.
     * 
     * @param project
     * @param combination
     * @param b		    True if checked (to be built), false otherwise 
     */
    public void setCombinationChecked(String project, Combination combination, boolean b){
	if(projectCheckedCombinations.get(project) != null){
	    projectCheckedCombinations.get(project).put(combination, b);
	} else {
	    Map<Combination,Boolean> temp = new HashMap<Combination, Boolean>();
	    projectCheckedCombinations.put(project, temp);
	    projectCheckedCombinations.get(project).put(combination, b);
	}
    }
    
    /**
     * Returns true if project was already added, false otherwise
     * 
     * @param project
     * @return 
     */
    public boolean containsProject(String project){
	if(projectCheckedCombinations.containsKey(project)){
	    return true;
	}
	return false;
    }
    
    /**
     * Adds project and sets all combinations to true (build by default).
     * 
     * @param project	
     * @param list	
     */
    public void addProject(String project, AxisList list){
	if(!projectCheckedCombinations.containsKey(project)){
	    Map<Combination,Boolean> temp = new HashMap<Combination, Boolean>();
	    projectCheckedCombinations.put(project, temp);
	    for(Combination c: list.list()){
		projectCheckedCombinations.get(project).put(c, true);
	    }
	    projectAxisList.put(project, list);
	}
    }
    
    
    
    public void addCombinationFilter(String project, String combinationFilter){
	projectCombinationFilter.put(project, combinationFilter);
	rebuildConfiguration(project);
    }
    
    /**
     * Removes Groovy expression provided for filtering and resets combinations to
     * false (as side effect)
     */
    public void removeCombinationFilter(String project){
	if(projectCombinationFilter.containsKey(project) &&
	    projectCombinationFilter.get(project) != null){
	    projectCombinationFilter.remove(project);
	}
	setAllCheckedFalse(project);
    }
    
    //FIXME: test
    private void rebuildConfiguration(String project){
	for(Combination combination : projectCheckedCombinations.get(project).keySet()){
	    if(projectCombinationFilter.get(project) != null && 
		    combination.evalGroovyExpression(projectAxisList.get(project), projectCombinationFilter.get(project))){
		projectCheckedCombinations.get(project).put(combination, true);
	    } else {
		projectCheckedCombinations.get(project).put(combination, false);
	    }
	}
    } 
    
}
