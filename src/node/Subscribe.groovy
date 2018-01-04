package node

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression

/**
 * Created by b_newyork on 2017-09-11.
 */
public class Subscribe {

    boolean error
    public String input
    public String handler
    public String capability
    ArrayList cap_val

    public Subscribe(ArrayList methodArgList){
        error = false

        this.input = handingArgs(methodArgList.get(0))

        if(methodArgList.size()  == 2){
            this.handler = handingArgs(methodArgList.get(1))
            this.capability = "default"

        }else if(methodArgList.size()  == 3){
            this.capability = handingArgs(methodArgList.get(1))
            this.handler = handingArgs(methodArgList.get(2))
        }

        setVal()


    }

    private void setVal(){
        cap_val = new ArrayList()
        if(capability.contains(".")){
            cap_val = capability.tokenize(".")
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

    String getInput() {
        return input
    }

    void setInput(String input) {
        this.input = input
    }

    String getCapability() {
        return capability
    }

    void setCapability(String capability) {
        this.capability = capability
    }

    String getHandler() {
        return handler
    }

    void setHandler(String handler) {
        this.handler = handler
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

}