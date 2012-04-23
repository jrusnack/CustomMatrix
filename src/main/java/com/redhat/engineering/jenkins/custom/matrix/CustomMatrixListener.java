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


import hudson.Extension;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import java.util.List;

/**
 * This registers the {@link Action}s to the side panel of the matrix project
 * and sets the Run.RedoRun object if it's actually a redo.
 * 
 * @author wolfgang
 */


@Extension
public class CustomMatrixListener extends RunListener<Run> {
	
    public CustomMatrixListener() {
        super(Run.class);
    }


    /**
     * Add the Custom Matrix link to the build context
     */
    @Override
    public void onCompleted(Run run, TaskListener listener) {
        /* Test for MatrixBuild and add to context */
        if (run instanceof MatrixBuild) {
            MatrixBuild build = (MatrixBuild)run;

            /*
             * check if action was already added to MatrixProject actions
             */
            List<CustomMatrixAction> list = build.getParent().getActions(CustomMatrixAction.class);
            if(list.isEmpty()){
                MatrixProject mp = build.getParent();
                mp.getActions().add(new CustomMatrixAction(mp));
            }
           
            CustomMatrixAction action = list.get(0);
            build.getActions().add(action);
            
        }

    }

}
