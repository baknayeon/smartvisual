package node

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import preferenceNode.Capability
import preferenceNode.Input
import preferenceNode.Subscribe

/**
 * Created by b_newyork on 2018-06-08.
 */
class SmartApp {

    //first
    HashMap definition = new HashMap<>();
    ArrayList preferenceList = new ArrayList()
    ArrayList subscribeList = new ArrayList()
    HashMap inputMap = new HashMap()
    HashMap methodMap = new HashMap()
    HashMap dynamicMethodMap = new HashMap<>();
    //first action
    HashMap sendMethodMap = new HashMap<>();

    private HashMap sendList = new HashMap<>();


    //second action
    HashMap ActionsCommandMap = new HashMap()
    HashMap calli2callerMap = new HashMap()


    //second dynamic
    ArrayList<Method> dynamicPageList =  new ArrayList<Method>()
    HashMap dynamicInputMap = new HashMap()

    public SmartApp(){
    }

    public boolean isitEventDevice(String name){
        for(Subscribe sub : subscribeList){
            if(sub.getInput().equals(name))
                return true
        }
        return false
    }

    public boolean isitActionDevice(String name){
       if(ActionsCommandMap.containsKey(name))
                return true
        return false
    }

    public void collectSendMethd(def message, String sendMethod){
        if(message in VariableExpression) {
            message = ((VariableExpression) message).variable
            if(sendList.containsKey(message)) {
                HashSet newSet = sendList.get(message)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                sendList.put(message, newSet);
            }
        }
    }

    public void collectSendMethd(def phone, def message, String sendMethod){
        if(phone in VariableExpression) {
            phone = ((VariableExpression) phone).variable
            if(sendList.containsKey(phone)){
                HashSet newSet = sendList.get(phone)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                sendList.put(phone, newSet);
            }
        }
        if(message in VariableExpression) {
            message = ((VariableExpression) message).variable
            if(sendList.containsKey(message)) {
                HashSet newSet = sendList.get(message)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                sendList.put(message, newSet);
            }
        }
    }

    public void pushSendMethod(String methodName){
        if(sendList.size() >0 ) {
            sendMethodMap.put(methodName, sendList)
            sendList = new HashSet<>()
        }
    }

    boolean isDynamicPage(ArrayList pageArgList){

        if( pageArgList.size() == 1)
            return true
        else
            return false
    }

    boolean isitDynamicPage(String method){
        return dynamicMethodMap.containsKey(method)
    }

    boolean checkSameInputOrNot(Input newInput){

        for(entry in preferenceList)
            if(entry instanceof Input){
                Input input = entry
                ArrayList list = input.getOptionInput();

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

    boolean addSubscribeList(Subscribe newSubscribe){

        for(entry in subscribeList){
            Subscribe subscribe = entry
            if (subscribe.getInput().equals(newSubscribe.getInput()))
                if (subscribe.getHandler().equals(newSubscribe.getHandler()))
                    if (subscribe.getCapability().equals(newSubscribe.getCapability()))
                        return
        }
        subscribeList.add(newSubscribe)
    }


}
