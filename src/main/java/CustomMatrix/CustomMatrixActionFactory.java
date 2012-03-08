/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomMatrix;

import hudson.Extension;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Register {@link Action} to the side panel for all matrix projects
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
        CustomMatrixAction newAction = new CustomMatrixAction();
	
	/**
	 * Test if project is matrix project
	 */
        if(ap instanceof MatrixProject){
	    actions.add(newAction);
	}
        return actions;
    }
}
