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

    /**
     * This is the class holding the information about a Custom Matrix state
     * retrieved from the form submit.
     * 
     * @author wolfgang
     */
    public class BuildState {
        /**
         * Determines whether a configuration should be reused or rebuild
         */
        Map<String, Boolean> configurations = new HashMap<String, Boolean>();

        public String uuid;

        public int rebuildNumber = 0;

        BuildState(String uuid) {
            this.uuid = uuid;
        }

        /**
         * Add a configuration to the build state
         * 
         * @param config A String representing the
         *            {@link hudson.matrix.MatrixConfiguration} given as its
         *            {@link hudson.matrix.Combination}
         * @param reuse A boolean to determine whether to reuse the
         *            {@link hudson.model.Run} or not
         */
        public void addConfiguration(Combination combination, boolean reuse) {
            this.configurations.put(combination.toString(), reuse);
        }

        /**
         * Remove the {@link BuildState} object from the Map.
         */
        public void remove() {
            CustomMatrixState.this.buildStates.remove(this.uuid);
        }

        /**
         * Remove a configuration from the build state
         * 
         * @param combination A {@link hudson.matrix.MatrixConfiguration} given
         *            as its {@link hudson.matrix.Combination}
         */
        public void removeConfiguration(Combination combination) {
            configurations.remove(combination.toString());

            /* Check if empty */
            if (configurations.isEmpty()) {
                remove();
            }
        }

        /**
         * Returns whether or not to rebuild the {@link hudson.model.Run}
         * If the combination is not in the database, the method returns true,
         * meaning the run will build.
         * 
         * @param combination A {@link hudson.matrix.MatrixConfiguration} given
         *            as its {@link hudson.matrix.Combination}
         * @return A boolean determining whether or nor to rebuild the
         *         {@link hudson.model.Run}
         */
        public boolean getConfiguration(Combination combination) {
            if (configurations.containsKey(combination.toString())) {
                return configurations.get(combination.toString());
            }

            return false;
        }
	
	/**
	 * Set all configurations to false, but don`t remove them. 
	 */
	public void setAllConfigurationFalse(){
	    for (String c : configurations.keySet()){
		configurations.put(c, false);
	    }
	}

        public String toString() {
            Set<String> keys = configurations.keySet();
            Iterator<String> it = keys.iterator();

            StringBuffer sb = new StringBuffer();

            while (it.hasNext()) {
                String s = it.next();
                sb.append(s + ": " + configurations.get(s) + "\n");
            }

            return sb.toString();
        }

        public int size() {
            return configurations.size();
        }
    }

    /**
     * The data of the class
     */
    private Map<String, BuildState> buildStates = new HashMap<String, BuildState>();

    /**
     * Return a specific BuildState given a uuid
     * 
     * @param uuid The uuid
     * @return The BuildState
     */
    public BuildState getBuildState(String uuid) {
        if (!buildStates.containsKey(uuid)) {
            buildStates.put(uuid, new BuildState(uuid));
        }

        return buildStates.get(uuid);
    }

    public boolean exists(String uuid) {
        return buildStates.containsKey(uuid);
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
