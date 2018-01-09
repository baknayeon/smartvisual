package node

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression

/**
 * Created by b_newyork on 2017-09-11.
 */
public class Subscribe {

    boolean error = false
    public String input
    public String handler
    public String capability
    public String cap_val

    public Subscribe(ArrayList methodArgList){
        int size = methodArgList.size();

        if(size == 2){
            this.input = handingArgs(methodArgList.get(0))
            this.capability = "default"
            this.handler = handingArgs(methodArgList.get(1))
        }else if(size >= 3) {
            this.input = handingArgs(methodArgList.get(0))
            this.capability = handingArgs(methodArgList.get(1))
            this.handler = handingArgs(methodArgList.get(2))
            if(capability.contains(".")){
                cap_val = capability.tokenize(".").get(1)
            }
        }
    }

    public Subscribe(String input, String handler){
        this.input = input
        this.handler = handler
        String capability = "default"
    }

    public Subscribe(String input, String capability, String handler) {
        this.input = input
        this.handler = handler
        this.capability = capability
    }

    private def handingArgs(ConstantExpression arg){
        return arg.getText()
    }

    private def handingArgs(PropertyExpression arg){
        return arg.getText()
    }

    private def handingArgs(VariableExpression argvex){
        return argvex.getName()
    }

    private def handingArgs(GStringExpression arg){
        return arg.verbatimText.toString()
    }

    public String getInput(){
        return input
    }
    public String getHandler(){
        return handler
    }
    public String getCapability(){
        return capability
    }

    public String getCap_val(){
        return cap_val
    }
}