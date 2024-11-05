package org.wso2.samples;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.util.JSONMergeUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

public class SimpleClassMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(SimpleClassMediator.class);

    private String variable1="10";

    private String variable2="10";

    public SimpleClassMediator(){}

    public boolean mediate(MessageContext mc) {
        log.info("Calling SimpleClassMediator.mediate()");

        return true;
    }

    public String getType() {
        return null;
    }

    public void setTraceState(int traceState) {
        traceState = 0;
    }

    public int getTraceState() {
        return 0;
    }

    public void setVariable1(String newValue) {
        variable1=newValue;
    }

    public String getVariable1() {
        return variable1;
    }

    public void setVariable2(String newValue){
        variable2=newValue;
    }

    public String getVariable2(){
        return variable2;
    }
}
