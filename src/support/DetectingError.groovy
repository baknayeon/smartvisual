package support

import node.ErrorSubscribe
import preferenceNode.Capability
import preferenceNode.Input
import node.SmartApp
import preferenceNode.Subscribe

/**
 * Created by b_newyork on 2017-12-05.
 */
class DetectingError {

    private ArrayList inputList
    private ArrayList subscribeList
    private HashSet methodSet

    ArrayList subErrorList
    ArrayList methodErrorList

    public DetectingError(SmartApp smartAppInfo) {

        inputList = smartAppInfo.getInputMap().values()
        subscribeList = smartAppInfo.getSubscribeList()
        methodSet = smartAppInfo.getMethodSet()
        methodErrorList = new ArrayList()
    }

    public void addMethodError(error){
        methodErrorList.addAll(error)
    }
    public void subscribe_error(){
        subErrorList = new ArrayList<>()

        for(Subscribe sub : subscribeList){

            String sub_input = sub.getInput()
            String sub_cap = sub.getCapability()

            boolean i = false
            boolean c = false
            boolean h = false

            if(sub_input.equals("location") || sub_input.equals("app")){
                i = true
                if(sub_cap.equals("location") || sub_input.equals("app")
                        || sub_cap.equals("mode") || sub_cap.equals("position") || sub_cap.equals("sunset") || sub_cap.equals("sunrise") || sub_cap.equals("sunriseTime") || sub_cap.equals("sunsetTime") )
                        c = true // capability of location, app
            }else{
                for(def list : inputList){
                    if(list in Input){
                        // input
                        Input input = list
                        String Input_input = input.getName()
                        if(sub_input.equals(Input_input)){
                            i = true
                            if (isItRightCapability(sub, input)) {
                                c = true
                            }
                            break
                        }
                    }
                }
            }

            // handler
            if(isItRightHandler(sub, methodSet)) {
                h = true
            }


            //make ErrorSubscribe
            if(!i || !c || !h){
                sub.setError(true)
                ErrorSubscribe e = new ErrorSubscribe(i, c, h, sub)
                subErrorList.add(e)
            }
        }
    }


    boolean isItRightCapability(Subscribe sub, Input input){

        String sub_cap = sub.getCapability()
        String sub_capVal = sub.getCap_val()

        Capability cap = CapHelper.getCap(input.getCapability())

        if(cap !=null){
            if(cap.getCapability().equals(sub_cap))
                return true
            else if(cap.checkAttrVal(sub_capVal))
                return true
            else if(cap.getAttribute().equals(sub_cap))
                return true
            else
                return false
        }
        return false
    }

    boolean isItRightHandler(Subscribe sub, HashSet methodSet){

        String handlerMethod = sub.getHandler().toString()

        return methodSet.contains(handlerMethod)
    }


}

