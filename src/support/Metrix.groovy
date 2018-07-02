package support

import node.DeviceAction
import node.SmartApp
import preferenceNode.Input

/**
 * Created by b_newyork on 2018-06-27.
 */
class Metrix {
    SmartApp smartApp

    public Metrix(SmartApp smartApp){
        this.smartApp = smartApp
    }

    public boolean[] evaluating() {
        boolean[] result = [false, false, false, false, false]
        String comment = ""
        if (simpleSmartApp())
            result[0] = true
        if(frequentEventSmartApp())
            result[1] = true
        if(((ArrayList)frequentActionSmartApp()).size() > 0)
            result[2] = true
        if(duplicatedSmartApp() != null)
            result[3] = true
        if(OnlysendingSMS())
            result[4] = true

        return result
    }

    private boolean simpleSmartApp() {

        int sub = smartApp.gettheNumof_sub()
        if (sub == 1) {
            if(duplicatedSmartApp() != null)
                return false
        }
        else
            return false

        return true
    }

    private String duplicatedSmartApp(){
        HashMap inputMap = smartApp.getInputMap()
        for (def input : inputMap.values()) {
            String name
            if (input in Input)
                name = input.getName()
            else if (input in String)
                name = input

            if (name != null) {
                if(smartApp.isitEventDevice(name) && smartApp.isitActionDevice(name)){
                    return name // true
                }
            }
        }
        return null //false
    }

    private boolean frequentEventSmartApp(){
        int sub = smartApp.gettheNumof_sub()
        if(sub > 1)
            return true
        else
            return false
    }

    private ArrayList frequentActionSmartApp(){

        ArrayList result = new ArrayList()
        HashMap Action = smartApp.getActionsCommandMap()
        for(String key : Action.keySet()){
            DeviceAction deviceAction = Action.get(key)
            ArrayList commands  = deviceAction.getCommands()
            if(commands.size() > 1)
                result.add(key)
            for(String c : commands){
                if(deviceAction.isItFrequentCommands(c))
                    result.add(key)


            }
        }
        return result
    }

    private boolean OnlysendingSMS(){
        int command = smartApp.total_actionCommand()
        int sendMethod =  smartApp.total_sendMethod()
        if(sendMethod > 0 && command == 0)
            return true
        return false
    }

}
