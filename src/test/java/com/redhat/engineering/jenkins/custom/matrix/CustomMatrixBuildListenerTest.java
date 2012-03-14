package com.redhat.engineering.jenkins.custom.matrix;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.model.ParameterValue;
import hudson.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.redhat.engineering.jenkins.custom.matrix.CustomMatrixState.BuildState;

import org.junit.BeforeClass;
import org.jvnet.hudson.test.HudsonTestCase;

public class CustomMatrixBuildListenerTest extends HudsonTestCase {
    private AxisList axes = null;

    private Combination c = null;

    @BeforeClass
    public void init() {

        axes = new AxisList(new Axis("dim1", "1", "2", "3"), new Axis("dim2", "a", "b", "c"));

        Map<String, String> r = new HashMap<String, String>();
        r.put("dim1", "1");
        r.put("dim2", "a");
        c = new Combination(r);
    }
    
    public void testReloaded() throws IOException, InterruptedException, ExecutionException {
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);

        List<ParameterValue> values = new ArrayList<ParameterValue>();
        /* UUID */
        String uuid = "myuuid";
        BuildState bs = CustomMatrixState.getInstance().getBuildState(uuid);

        MatrixBuild mb = mp.scheduleBuild2(0).get();
        MatrixRun mr = mb.getRun(c);
        Result r = mr.getResult();

        assertNotNull(mb);
    }
}
