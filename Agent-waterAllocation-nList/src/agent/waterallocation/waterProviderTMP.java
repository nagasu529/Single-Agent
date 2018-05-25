/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.waterallocation;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Hashtable;

/**
 *
 * @author chiewchk
 */
public class waterProviderTMP extends Agent{
    private Hashtable waterUsageList;
    //The value of water reduction policy
    private String pctWaterReduction;
    
    //The list of know farmer agents
    private AID[] farmerAgents;
    
    protected void setup(){
        System.out.println("Water policy agent is started");
        
        Object[] args = getArguments();
        if(args != null && args.length > 0){
            pctWaterReduction = (String) args[0];
            System.out.println("Water reduction policy need to reduce "+ pctWaterReduction + " % of water usage");
            
            //Adding TickerBehaviour is that a water reduction rate from farmer agents every minute.
            addBehaviour(new TickerBehaviour(this, 30000) {
                protected void onTick() {
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("Farmer agent");
                    template.addServices(sd);
                    try{
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("Found the following farmer agents:");
                        farmerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            farmerAgents[i] = result[i].getName();
                            System.out.println(farmerAgents[i].getName());
                        }
                    }
                    catch(FIPAException fe){
                        fe.printStackTrace();
                    }
                    
                    //Perform the request
                    myAgent.addBehaviour(new RequestPerformer());
                    
                }
            });
        }
        else{
            System.out.println("No farmer");
            doDelete();
        }  
    }
    
    //Put agent clean-up operations
    
    protected void takedown(){
        System.out.println("Water policy agent terminated");
    }
    
    //Inner class for water provider agent
    /**
     * Inner class RequestPerformer.
     * This is the behavior used by water provider agent to receive 
     * water reduction rate and others informations from farmer agents. 
     */
    private class RequestPerformer extends Behaviour{
        private AID farmerName;         //agent who provide water reduction rate.
        private double pctgFarmReduction;      //the percentage of reduction from farmer agent (single farm value)
        private int repliesCnt = 0;             //
        private MessageTemplate mt;
        private int step = 0;
        
        public void action(){
            switch (step){
                case 0:
                //send the water reduction rates  to all farmers.
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < farmerAgents.length; ++i) {
                        cfp.addReceiver(farmerAgents[i]);
                    }
                    cfp.setContent(pctWaterReduction);
                    cfp.setConversationId("water-user");
                    cfp.setReplyWith("cpf"+System.currentTimeMillis());   //Unique value
                    myAgent.send(cfp);
                    
                    //Prepare the temlate to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("water-user"), 
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    /*mt = MessageTemplate.and(MessageTemplate.MatchConversationId("water-user"), 
                            MessageTemplate.MatchReplyTo(cfp.getReplyWith()));*/
                    step =1;
                    break;
                case 1:
                    //Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        //Reply recieved
                        if(reply.getPerformative()==ACLMessage.PROPOSE){
                            //This is an water reduction percentage from farmer
                            int farmWaterRedPctg = Integer.parseInt(reply.getContent());
                            step = 2;
                        }
                    }
                    else{
                        block();
                    }
                    break;
            }
        }
        
        public boolean done(){
            if(step == 2){
                System.out.println("all farmers send reduction rate to water provider");
            }
            return step == 2;
        }
    }
}
