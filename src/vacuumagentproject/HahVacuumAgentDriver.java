package vacuumagentproject;

import java.util.Random;
import java.util.logging.*;

/**
 * @author Hayden Holbrook
 */
public class HahVacuumAgentDriver extends VacuumAgentDriver {

  private static Logger logger = Logger.getLogger("logger");

  private static void runSimulation(int simNumber, String[] args) {

    try {
      FileHandler fh = new FileHandler(String.format("logs/%03d_sim.log", simNumber), true);
      fh.setFormatter(new SimpleFormatter());
      logger.addHandler(fh);
      logger.setLevel(Level.ALL);
      logger.log(Level.INFO, String.format("Simulation Number %d\n", numSims));

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
      geometry = null;
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
      System.out.println("Final Environment:");
      System.out.println(vEnviron);

      int count = 1;
      for (VacuumTrace entry : trace) {
        // System.out.printf("%d: %s", count, entry);
        logger.log(Level.INFO, String.format("%d: %s", count, entry));
        count++;
      }
      logger.removeHandler(fh);
      fh.close();
    } catch (Exception e) {
      System.err.println("Something went wrong.");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    for (int i = 1; i <= numSims; i++) {
      runSimulation(i, args);
    }
  } // main
}
