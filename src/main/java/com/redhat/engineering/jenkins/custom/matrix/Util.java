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

import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.model.StringParameterValue;

import java.util.List;


public abstract class Util {

//	public static BuildState getBuildStateFromRun( Run run ) {
//		
//        List<ParametersAction> actionList = run.getActions(ParametersAction.class);
//
//        if (actionList.size() == 0) {
//            return null;
//        }
//        
//        List<ParameterValue> pvs = actionList.get(0).getParameters();
//        
//        /* If the list is null */
//        if( pvs == null ) {
//        	return null;
//        }
//        
//        StringParameterValue uuid = (StringParameterValue)getParameterValue(pvs, Definitions.__UUID);
//        
//        /* If the uuid is not defined, return true */
//        if( uuid == null ) {
//        	return null;
//        }
//        
//        return CustomMatrixState.getInstance().getBuildState(uuid.value);
//	}
	
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
