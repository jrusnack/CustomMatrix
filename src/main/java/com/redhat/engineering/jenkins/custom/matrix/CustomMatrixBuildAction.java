/*
 * The MIT License
 *
 * Copyright 2012 jrusnack.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.redhat.engineering.jenkins.custom.matrix;

import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Actionable;
import java.io.IOException;
import javax.servlet.ServletException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author jrusnack
 */
public class CustomMatrixBuildAction implements Action{
    private MatrixBuild build;
    private CustomMatrixProjectAction projectAction;

    public CustomMatrixBuildAction(MatrixBuild build, CustomMatrixProjectAction projectAction){
        this.build = build;
        this.projectAction = projectAction;
    }

    public String getIconFileName() {
        return projectAction.getIconFileName();
    }

    public String getDisplayName() {
        return projectAction.getDisplayName();
    }

    public String getUrlName() {
        return projectAction.getUrlName();
    }

    public MatrixBuild getBuild(){
        return build;
    }
    
    public boolean isCombinationChecked(Combination c){
        return projectAction.isCombinationChecked(c);
    }
    
    
    public boolean combinationExists( MatrixProject mp, Combination c ){
        return projectAction.combinationExists(build, c);
    }
    public boolean combinationExists( AbstractBuild<?, ?> build, Combination c ){
        return projectAction.combinationExists(build, c);
    }    
    
    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws ServletException,
            IOException, InterruptedException {
        projectAction.doConfigSubmit(req, rsp);    
    }
    
    public boolean getMatrixChecked(){
        return projectAction.getMatrixChecked();
    }
    
    public boolean getCombinationFilterChecked(){
        return projectAction.getCombinationFilterChecked();
    }
    
    public String getCombinationFilter(){
        return projectAction.getCombinationFilter();
    }
    
    public String getAxes(){
        return projectAction.getAxes();
    }
    
}
