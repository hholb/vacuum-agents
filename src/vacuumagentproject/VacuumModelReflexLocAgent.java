
package vacuumagentproject;

import java.util.*;

/**
 *
 * @author Marietta E. Cameron
 */
public class VacuumModelReflexLocAgent extends VacuumAgent {
    
    VacuumModelReflexLocAgent (){
   
    }
     @Override
    public VacuumAction getAction(VacuumPercept percept){
        
        if (percept instanceof VacuumLocPercept)
            return getActionModelReflex((VacuumLocPercept) percept);
        else {
            System.out.println("Error:  Expected a Location Percept!!");
            return VacuumAction.STOP;
        }
    }
    private VacuumAction getActionModelReflex(VacuumLocPercept percept){
        if (percept.currentStatus == Status.DIRTY)
            return VacuumAction.SUCK;
        else 
            return VacuumAction.STOP;
        
    } 
   }//VacuumModelReflexLocAgent
