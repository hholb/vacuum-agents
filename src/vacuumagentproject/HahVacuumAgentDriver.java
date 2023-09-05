package vacuumagentproject;

import java.util.Random;
import java.util.logging.*;

/**
 * @author Hayden Holbrook
 */
public class HahVacuumAgentDriver extends VacuumAgentDriver {

  private static Logger logger = Logger.getLogger("logger");

  public static void main(String[] args) {

    FileHandler fh;

    try {
      fh = new FileHandler("sim.log", true);
      logger.addHandler(fh);
      logger.setLevel(Level.ALL);

      // parse and set parameters using command line args
      parseCommandLine(args);
      if (setAgentLocRandomly) {
        Random gen = new Random();
        if (geometry == null) {
          agentX = gen.nextInt(maxWidth);
          agentY = gen.nextInt(maxHeight);
        } else {
          agentX = gen.nextInt(geometry[0].length());
          agentY = gen.nextInt(geometry.length);
        }
      }

      // initialize environment
      VacuumEnvironment vEnviron;
      if (geometry == null) {
        vEnviron = new VacuumEnvironment(maxWidth, maxHeight, dirtProb, agent, agentX, agentY);
      } else {
        vEnviron = new VacuumEnvironment(geometry, agent, agentX, agentY);
      }

      if (useVisualizer) {
        initialFloor = vEnviron.getFloor(false);
      } else {
        System.out.println("Initial Environment:");
        System.out.println(vEnviron);
      }
      int timeSteps = vEnviron.simulate(maxTimeSteps, targetPerformance, usePercept);
      trace = vEnviron.getTrace();
      if (useVisualizer) {
        System.out.println("Visualizer on");
        visualize();
      } else {
        System.out.println("Final Environment:");
        System.out.println(vEnviron);

        int count = 1;
        for (VacuumTrace entry : trace) {
          System.out.printf("%d: %s", count, entry);
          logger.log(Level.INFO, String.format("%d: %s", count, entry));
          count++;
        }
      }
    } catch (Exception e) {
      System.err.println("Something went wrong.");
      System.exit(1);
    }
  } // main
}
