package support

import node.Capability
import node.Input
import node.Section
import node.Subscribe
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Created by b_newyork on 2017-09-01.
 */
class Helper {

    String methodCall= ""
    Map allCommands

    public Helper(){
        allCommands = new HashMap()
        loadCapRef()
    }

    private def loadCapRef() {

        def capsAsPerSamsungFile = "./" + "Capabilities.csv"
        def file = new File(capsAsPerSamsungFile)

        file.splitEachLine(",") { fields ->

            Capability cap =  new Capability()

            String Cap = fields[1]?.toString()
            cap.setCapability(Cap)

            String dev = fields[2]?.toString()
            cap.setDevice(dev)

            fields[3]?.split(" ")?.each {
                cap.cap_val.add(it.toString())
            }

            allCommands[Cap] =  cap
        }
    }

    private boolean isItRightCapability(Subscribe sub, Input input){

        String sub_capVal = sub.getCap_val()
        String input_cap = input.getCapability()

        Capability cap = allCommands[input_cap]


        if(cap !=null){
            if(cap.checkVal(sub_capVal))
                return true
            else
                return false
        }
        return false
    }

    private boolean isItRightHandler(Subscribe sub, ArrayList methodList){

        for(methodName in methodList){
            if(methodName.equals(sub.getHandler().toString()))
                return true
        }
        return false
    }

    boolean isDynamicPage(ArrayList pageArgList){

        if( pageArgList.size() == 1)
            return true
        else
            return false

    }

    boolean checkSameInputOrNot(ArrayList preferenList, Input newInput){

        for(entry in preferenList)
            if(entry instanceof Input){
                Input input = entry
                ArrayList list = input.getOption();

                if (input.getName().equals(newInput.getName()))
                    return false
                else if(list.size() > 0){
                    if(checkSameInputOrNot(list ,newInput)){

                    }else{
                        return false
                    }
                }
            }

        return true
    }

    boolean checkSameSubscribeOrNot(ArrayList subscribeList, Subscribe newSubscribe){

        for(entry in subscribeList){
            if(entry instanceof Subscribe){
                Subscribe subscribe = entry
                if (subscribe.getInput().equals(newSubscribe.getInput()))
                    if (subscribe.getHandler().equals(newSubscribe.getHandler()))
                        if (subscribe.getCapability().equals(newSubscribe.getCapability()))
                            return false
            }
        }

        return true
    }

}
