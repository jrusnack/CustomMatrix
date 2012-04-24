/*
 * The MIT License
 *  
 * Copyright 2012 Red Hat, Inc.
 * Copyright 2011 Praqma A/S.
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

import hudson.Extension;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
@Extension
public class CustomMatrixActionFactory extends TransientProjectActionFactory{
    
    /**
    * {@inheritDoc}
    */

    @Override
    public Collection<? extends Action> createFor(AbstractProject ap) {
        ArrayList<Action> actions = new ArrayList<Action>();

	/**
	* Test if project is matrix project
	*/
        if(ap instanceof MatrixProject){
            CustomMatrixAction newAction = new CustomMatrixAction((MatrixProject)ap);
            actions.add(newAction);
	}
        return actions;
    }
}