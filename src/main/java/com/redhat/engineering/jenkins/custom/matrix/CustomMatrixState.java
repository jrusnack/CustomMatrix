/*
 *  The MIT License
 *
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
    private static final CustomMatrixState instance = new CustomMatrixState();

    private CustomMatrixState() {
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
}
