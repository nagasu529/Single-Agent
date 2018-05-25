/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.waterallocation;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;
/**
 *
 * @author chiewchk
 */
public class Farmer extends Agent{
    private FarmerGui myGui;
    
    protected void setup(){
        System.out.println(getAID()+" is ready");
        myGui = new FarmerGui();
        myGui.setVisible(true);
    }
}
