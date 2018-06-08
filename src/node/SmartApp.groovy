package node

import org.codehaus.groovy.ast.expr.VariableExpression

/**
 * Created by b_newyork on 2018-06-08.
 */
class SmartApp {

    //first
    HashMap definition = new HashMap<>();
    ArrayList preferenceList = new ArrayList()
    ArrayList subscribeList = new ArrayList()
    HashMap inputList = new ArrayList()
    HashMap dynamicMethodMap = new HashMap<>();
    HashMap sendMethodMap = new HashMap<>();


    //second action
    HashMap ActionsCommandMap = new HashMap()
    HashMap calli2callerMap = new HashMap()


    //second dynamic
    ArrayList<Method> dynamicPageList =  new ArrayList<Method>()
    HashMap dynamicInputMap = new HashMap()

    public void addSendMethd(VariableExpression arg, String method){
        sendMethodMap.put()
    }

    public void addSendMethd(def phone, def message, String method){
        sendMethodMap.put()
    }
    boolean isItRightCapability(Subscribe sub, Input input){

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

    boolean isItRightHandler(Subscribe sub, HashMap methodMap){

        String handlerMethod = sub.getHandler().toString()

        return methodMap.containsKey(handlerMethod)
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

    boolean addSubscribeList(Subscribe newSubscribe){

        for(entry in subscribeList){
            if(entry instanceof Subscribe){
                Subscribe subscribe = entry
                if (subscribe.getInput().equals(newSubscribe.getInput()))
                    if (subscribe.getHandler().equals(newSubscribe.getHandler()))
                        if (subscribe.getCapability().equals(newSubscribe.getCapability()))
                            return
            }
        }

        subscribeList.add(newSubscribe)
    }


    public static Map allCommands = new HashMap()

    def loadCapRef() {

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
}
