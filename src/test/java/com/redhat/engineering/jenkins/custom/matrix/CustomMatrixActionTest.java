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

import com.redhat.engineering.jenkins.custom.matrix.Definitions;
import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.redhat.engineering.jenkins.custom.matrix.CustomMatrixProjectAction;
import com.redhat.engineering.jenkins.custom.matrix.CustomMatrixProjectAction.BuildType;
import net.sf.json.JSONObject;

import org.jvnet.hudson.test.HudsonTestCase;

public class CustomMatrixActionTest extends HudsonTestCase {
    
    public void testGetDisplayName()throws IOException {
	init();
	MatrixProject mp = createMatrixProject( "test" );
        CustomMatrixProjectAction mra = new CustomMatrixProjectAction(mp);

        assertEquals(mra.getDisplayName(), Definitions.__DISPLAY_NAME);
    }

    public void testGetIconFileName()throws IOException {
	init();
	MatrixProject mp = createMatrixProject( "test" );
        CustomMatrixProjectAction mra = new CustomMatrixProjectAction(mp);

        assertEquals(mra.getIconFileName(), Definitions.__ICON_FILE_NAME);
    }

    public void testGetUrlName() throws IOException{
	init();
	MatrixProject mp = createMatrixProject( "test" );
        CustomMatrixProjectAction mra = new CustomMatrixProjectAction(mp);

        assertEquals(mra.getUrlName(), Definitions.__URL_NAME);
    }



    public void testBuildType() {
        BuildType bt = BuildType.MATRIXBUILD;
    }
    
    public void testCombinationExists() throws InterruptedException, ExecutionException, IOException {
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);
        List<ParameterDefinition> list = new ArrayList<ParameterDefinition>();
        list.add(new StringParameterDefinition("key", "value"));
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(list);
        mp.addProperty(pdp);

        /*
         * Create some parameters to test continuation of parameters from reused
         * to new build
         */
        List<ParameterValue> values = new ArrayList<ParameterValue>();
        values.add(new StringParameterValue("key", "value"));

        MatrixBuild mb = mp.scheduleBuild2(0, new Cause.UserCause(), new ParametersAction(values)).get();
        
        CustomMatrixProjectAction mra = new CustomMatrixProjectAction(mp);
        
        assertTrue( mra.combinationExists(mb, c_good) );
    }
    
    public void testCombinationExists2() throws InterruptedException, ExecutionException, IOException {
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);
        List<ParameterDefinition> list = new ArrayList<ParameterDefinition>();
        list.add(new StringParameterDefinition("key", "value"));
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(list);
        mp.addProperty(pdp);

        /*
         * Create some parameters to test continuation of parameters from reused
         * to new build
         */
        List<ParameterValue> values = new ArrayList<ParameterValue>();
        values.add(new StringParameterValue("key", "value"));

        MatrixBuild mb = mp.scheduleBuild2(0, new Cause.UserCause(), new ParametersAction(values)).get();
        
        CustomMatrixProjectAction mra = new CustomMatrixProjectAction(mp);
        
        assertFalse( mra.combinationExists(mb, c_bad) );
    }    
    

    private AxisList axes = null;

    private Combination c_good = null;
    private Combination c_bad = null;

    public void init() {

        axes = new AxisList(new Axis("dim1", "1", "2"), new Axis("dim2", "a", "b"));

        Map<String, String> r = new HashMap<String, String>();
        r.put("dim1", "1");
        r.put("dim2", "a");
        c_good = new Combination(r);
        
        Map<String, String> r2 = new HashMap<String, String>();
        r2.put("dim1", "11");
        r2.put("dim2", "a");
        c_bad = new Combination(r2);
    }


}
