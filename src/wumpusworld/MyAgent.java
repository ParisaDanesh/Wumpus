package wumpusworld;
import NeuralNetwork.*;

import java.util.Arrays;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    int rnd;

    /**
     * Creates a new instance of your solver agent.
     *
     * @param world Current world state
     */
    public MyAgent(World world)
    {
        w = world;
    }

    public double room_situation(World w, int x, int y, int ext_x, int ext_y){
        int new_x = x + ext_x;
        int new_y = y + ext_y;

        double tmp = 0;

        if (w.isValidPosition(new_x, new_y)){
            if (w.isVisited(new_x, new_y))
                tmp = 1;  //visited
            else
                tmp =0;  //unvisited
        }else
            tmp = 0.5; //wall

        return tmp;
    }

    /**
     * Asks your solver agent to execute an action.
     */

    public int nn(){
        NeuralNetwork net;
        int index=0;

        int cX = w.getPlayerX();
        int cY = w.getPlayerY();

        double north, south, west, east;
        north = room_situation(w, cX, cY, 0, 1); //up
        south = room_situation(w, cX, cY, 0, -1); //down
        west = room_situation(w, cX, cY, -1, 0); //left
        east = room_situation(w, cX, cY, 1, 0); //right

        // breeze, stench, glitter
        double breeze, stench, glitter;
        if (w.hasBreeze(cX, cY)) {
            breeze = 1;
        }
        else {
            breeze = 0;
        }

        if (w.hasStench(cX, cY)) {
            stench = 1;
        }
        else {
            stench = 0;
        }

        if (w.hasGlitter(cX, cY)) {
            glitter = 1;
        }
        else {
            glitter = 0;
        }

        // prepare an input array
        double[] input = new double[]{north, east, south, west, breeze, stench, glitter};
        System.out.println(Arrays.toString(input) + " - " + cX + " - "+ cY);

        try {
            net = NeuralNetwork.loadNetwork("saved_model.txt");
            index = Utils.indexOfHighestValue(net.calculate(input));
            return index;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    public void doAction()
    {
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();

        int index = nn();
        System.out.println(index);

        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }

        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }

        //Test the environment
        if (w.hasBreeze(cX, cY))
        {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(cX, cY))
        {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(cX, cY))
        {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT)
        {
            switch (index){
                case 0: // up
                    w.doAction(World.A_TURN_LEFT);
                    w.setVisited(cX, cY);
//                        w.doAction(World.A_MOVE);
                    break;
                case 1: // right
                    w.doAction(World.A_MOVE);
                    w.setVisited(cX, cY);
                    break;
                case 2:  // down
                    w.doAction(World.A_TURN_RIGHT);
//                        w.doAction(World.A_MOVE);
                    w.setVisited(cX, cY);
                    break;
                case 3:  //left
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
//                        w.doAction(World.A_MOVE);
                    break;
            }
            System.out.println("I am facing Right");
        }


        if (w.getDirection() == World.DIR_LEFT)
        {
            System.out.println("I am facing Left");
            switch (index){
                case 0: // up
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_RIGHT);
//                        w.doAction(World.A_MOVE);
                    break;
                case 1: // right
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_RIGHT);
                    w.doAction(World.A_TURN_RIGHT);
                        w.doAction(World.A_MOVE);
                    break;
                case 2:  // down
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_LEFT);
//                        w.doAction(World.A_MOVE);
                    break;
                case 3:  //left
                    w.setVisited(cX, cY);
                    w.doAction(World.A_MOVE);
                    break;
            }
        }
        if (w.getDirection() == World.DIR_UP)
        {
            System.out.println("I am facing Up");
            switch (index){
                case 0: // up
                    w.setVisited(cX, cY);
                    w.doAction(World.A_MOVE);
                    break;
                case 1: // right
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_RIGHT);
                        w.doAction(World.A_MOVE);
                    break;
                case 2:  // down
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
//                        w.doAction(World.A_MOVE);
                    break;
                case 3:  //left
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                    break;
            }
        }
        if (w.getDirection() == World.DIR_DOWN)
        {
            System.out.println("I am facing Down");
            switch (index){
                case 0: // up
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
                        w.doAction(World.A_MOVE);
                    break;
                case 1: // right
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                    break;
                case 2:  // down
                    w.setVisited(cX, cY);
                    w.doAction(World.A_MOVE);
                    break;
                case 3:  //left
                    w.setVisited(cX, cY);
                    w.doAction(World.A_TURN_RIGHT);
                    w.doAction(World.A_MOVE);
                    break;
            }

        }
    }
    
    /**
     * Genertes a random instruction for the Agent.
     */
    public int decideRandomMove()
    {
      return (int)(Math.random() * 4);
    }
    
    
}

