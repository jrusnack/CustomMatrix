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

import java.util.logging.Logger;

import com.redhat.engineering.jenkins.custom.matrix.CustomMatrixState.BuildState;

import hudson.Extension;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixBuild;
import hudson.matrix.listeners.MatrixBuildListener;

@Extension
public class CustomMatrixBuildListener extends MatrixBuildListener{
	
	private static Logger logger = Logger.getLogger(CustomMatrixBuildListener.class.getName());
	
	public boolean doBuildConfiguration(MatrixBuild b, MatrixConfiguration c) {
        BuildState bs = Util.getBuildStateFromRun(b);
        if( bs == null ) {
        	logger.severe("I didn't get");
        	return true;
        }
        
        logger.severe("I got " + bs);
        
        return bs.getConfiguration(c.getCombination());
	}
}
