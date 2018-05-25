/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.waterallocation;
import jade.core.Agent;

/**
 *
 * @author chiewchk
 */
public class HelloAgent extends Agent {
    
    protected void setup(){
        //print out message
        System.out.println("Hello!- agent");
        System.out.println("Farming agent:" + getLocalName()+" is started");
        //doDelete();
    }
}
