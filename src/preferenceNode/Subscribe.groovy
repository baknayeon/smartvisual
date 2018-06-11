package preferenceNode

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
            def one = methodArgList.get(0)
            def second = methodArgList.get(1)
            this.input = handingArgs(one)
            this.capability = ""
            this.handler = handingArgs(second)
        }else if(size >= 3) {
            def one = methodArgList.get(0)
            def second = methodArgList.get(1)
            def third = methodArgList.get(2)
            this.input = handingArgs(one)
            this.capability = handingArgs(second)
            this.handler = handingArgs(third)
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

    private def handingArgs(def arg){
        return "null"
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