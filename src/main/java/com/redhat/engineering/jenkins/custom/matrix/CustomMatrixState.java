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
    // FIXME
    private Map<String,Map<String,Boolean>> projectCheckedCombinations;
    private static final CustomMatrixState instance = new CustomMatrixState();

    private CustomMatrixState() {
	projectCheckedCombinations = new HashMap<String, Map<String, Boolean>>();
    }

    public static CustomMatrixState getInstance() {
        return instance;
    }

    
    public boolean isCombinationChecked(String project, String combination){
	if(projectCheckedCombinations.containsKey(project) && projectCheckedCombinations.get(project).containsKey(combination)){
	    return projectCheckedCombinations.get(project).get(combination);
	}	    
	return true;
    }
    
    public void setAllCheckedFalse(String project){
	if(projectCheckedCombinations.containsKey(project)){
	    for(String combination : projectCheckedCombinations.get(project).keySet()){
		projectCheckedCombinations.get(project).put(combination, false);
	    }
	}
    }
    
    public void setCombinationChecked(String project, String combination, boolean b){
	if(projectCheckedCombinations.get(project) != null){
	    projectCheckedCombinations.get(project).put(combination, b);
	} else {
	    Map<String,Boolean> temp = new HashMap<String, Boolean>();
	    projectCheckedCombinations.put(project, temp);
	    projectCheckedCombinations.get(project).put(combination, b);
	}
    }
    
    public boolean containsProject(String project){
	if(projectCheckedCombinations.containsKey(project)){
	    return true;
	}
	return false;
    }
    
    public void addProject(String project, AxisList list){
	if(!projectCheckedCombinations.containsKey(project)){
	    Map<String,Boolean> temp = new HashMap<String, Boolean>();
	    projectCheckedCombinations.put(project, temp);
	    for(Combination c: list.list()){
		projectCheckedCombinations.get(project).put(c.toString(), true);
	    }
	}
    }
}
