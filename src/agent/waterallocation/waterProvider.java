/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.waterallocation;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Hashtable;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author chiewchk
 */
public class waterProvider extends Agent{
    //Water usage on farm (result table is that map farName and water reduction value)
    private Hashtable waterUsageTable;
    //The value of water reduction policy
    private String pctWaterReduction;
    
    //The list of know farmer agents
    private AID[] farmerAgents;
    
    protected void setup(){
        //Create result table
        waterUsageTable = new Hashtable();
        
        System.out.println("Water policy agent is started");
        Object[] args = getArguments();
        if(args != null && args.length > 0){
            pctWaterReduction = (String) args[0];
            System.out.println("Water reduction policy need to reduce "+ pctWaterReduction + " % of water usage");
        }    
        // Register the water reduction rate in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("water-policy");
        sd.setName("water-reduction-rate");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        //Water provider behaviour
        
    }
    
    protected void takeDown(){
        //Deregister from yellow pages
        try{
            DFService.deregister(this);
        }
        catch(FIPAException fe){
            fe.printStackTrace();
        }
    }
    
    //Update result table (water reduction rate)
    public void updateWaterUsageTable(final String farmerName, final double wtrReduction){
        addBehaviour(new OneShotBehaviour() {
            public void action() {
               waterUsageTable.put(farmerName, new Double(wtrReduction));
                System.out.println(farmerName + " inserted into Water reduction table = "+ wtrReduction);
            }
        });
    }
    /*
    Private class for cyclic behaviour
    
    PolicyToFarmers: This is the behaviour for sending to farmers to reduce their farm water used.
    */
}
