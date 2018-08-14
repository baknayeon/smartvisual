package support

import node.DeviceAction
import node.SmartApp
import preferenceNode.Input

/**
 * Created by b_newyork on 2018-06-27.
 */
class Matrix {
    private SmartApp smartApp
    String duplicatedDevice
    String frequentAction
    String smsArgs

    public Matrix(SmartApp smartApp){
        this.smartApp = smartApp
        duplicatedDevice = ""
        frequentAction = ""
        smsArgs = ""
    }

    public boolean[] evaluating() {
        boolean[] result = [false, false, false, false, false]

        if (simpleSmartApp())
            result[0] = true
        if(frequentEventSmartApp())
            result[1] = true
        if(frequentActionSmartApp())
            result[2] = true
        if(duplicatedDevice())
            result[3] = true
        if(OnlysendingSMS())
            result[4] = true

        return result
    }

    private boolean simpleSmartApp() {

        int sub = smartApp.gettheNumof_sub()
        if (sub == 1) {
            if(duplicatedDevice())
                return false
        }
        else
            return false

        return true
    }

    private boolean duplicatedDevice(){

        ArrayList result = new ArrayList()
        HashMap inputMap = smartApp.getInputMap()
        for (def input : inputMap.values()) {
            String name
            if (input in Input)
                name = input.getName()
            else if (input in String)
                name = input

            if (name != null) {
                if(smartApp.isitEventDevice(name) && smartApp.isitActionDevice(name)){
                    duplicatedDevice = duplicatedDevice +  name +" "

                }
            }
        }

        if(duplicatedDevice.length() > 0 )
            return true
        else
            return false
    }

    private boolean frequentEventSmartApp(){
        int sub = smartApp.gettheNumof_sub()
        if(sub > 1)
            return true
        else
            return false
    }

    private boolean frequentActionSmartApp(){

        ArrayList result = new ArrayList()
        HashMap Action = smartApp.getActionsMap()
        for(String key : Action.keySet()){
            DeviceAction deviceAction = Action.get(key)
            ArrayList commands  = deviceAction.getCommands()
            if(commands.size() > 1) { // on, off
                frequentAction = frequentAction + key +" "
                result.add(key)
                continue
            }
            for(String c : commands){ //on, on
                if(deviceAction.isItFrequentCommands(c))
                    frequentAction = frequentAction + key +" "
            }
        }



        if(frequentAction.length() > 0)
            return true
        else
            return false
    }

    private boolean OnlysendingSMS(){
        int command = smartApp.total_actionCommand()
        int sendMethod =  smartApp.total_sendMethod()
        HashMap sendMap = smartApp.getSendMethodMap()
        for(String key : sendMap.keySet()){
            HashMap args = sendMap.get(key)
            for(String arg : args.keySet()){
                smsArgs = smsArgs + arg+" "
            }
        }
        if(sendMethod > 0 && command == 0)
            return true
        return false
    }

}
