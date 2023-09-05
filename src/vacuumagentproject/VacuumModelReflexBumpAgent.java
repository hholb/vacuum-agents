
package vacuumagentproject;

/**
 *
 * @author Archeress1966
 */
import java.util.*;

public class VacuumModelReflexBumpAgent extends VacuumAgent{

    
    VacuumModelReflexBumpAgent(){
         //update as you see fit
    }
    @Override
    public VacuumAction getAction(VacuumPercept percept){
        
        if (percept instanceof VacuumBumpPercept)
            return getActionModelReflex((VacuumBumpPercept) percept);
        else {
            System.out.println("Error:  Expected a Bump Percept!!");
            return VacuumAction.STOP;
        }
    }
    
   
   private VacuumAction getActionModelReflex(VacuumBumpPercept percept){
       //update this method  
	   
	   if (percept.currentStatus == Status.DIRTY)           
            return VacuumAction.SUCK;
         else
             return VacuumAction.STOP;
    
    }  
   }
