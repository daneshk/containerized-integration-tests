package org.wso2.samples;

import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
    
public class SimpleClassMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(SimpleClassMediator.class);

    private String variable1="10";
    private String variable2="10";

    public boolean mediate(MessageContext context) {
        log.info("SimpleClassMediator is mediating the message");
        try {
            org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context).getAxis2MessageContext();
            // construct json object
            String jsonPayload = "{\"variable1\":\"" + variable1 + "\",\"variable2\":\"" + variable2 + "\"}";
            // set json object to message context
            JsonUtil.newJsonPayload(axis2MessageContext, jsonPayload, true, true);
        } catch (Exception e) {
            System.err.println("Error: " + e);
            return false;
        }
        return true;
    }

    public String getVariable1() {
        return variable1;
    }

    public void setVariable1(String variable1) {
        this.variable1 = variable1;
    }

    public String getVariable2() {
        return variable2;
    }

    public void setVariable2(String variable2) {
        this.variable2 = variable2;
    }
}
