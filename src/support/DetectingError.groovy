package support

import node.ErrorSubscribe
import node.Input
import node.Subscribe

/**
 * Created by b_newyork on 2017-12-05.
 */
class DetectingError {

    private ArrayList preferenceList
    private ArrayList subscribeList
    private HashMap methodList

    ArrayList subErrorList
    ArrayList methodErrorList

    public DetectingError(ArrayList pre, ArrayList sub, HashMap method) {
        preferenceList = pre
        subscribeList = sub
        methodList = method
        methodErrorList = new ArrayList()
    }

    public void addMethodError(error){
        methodErrorList.addAll(error)
    }
    public void subscribe_error(){
        subErrorList = new ArrayList<>()
        //Helper helper = new Helper()

        subscribeList.each { Subscribe sub ->

            String sub_input = sub.getInput()
            String sub_cap = sub.getCapability()

            boolean i = false
            boolean c = false
            boolean h = false

            if(sub_input.equals("location") || sub_input.equals("app")){
                i = true
                if(sub_cap.equals("default") ||
                        sub_cap.equals("position") || sub_cap.equals("sunriseTime") || sub_cap.equals("sunsetTime") )
                        c = true // capability of location, app
                else
                    sub.setError(true)
            }else{
                int index  = 0
                while(index < preferenceList.size()){
                    def list = preferenceList.get(index)

                    if(list in Input){

                        // input
                        String Input_input = ((Input)list).getName()
                        if(sub_input.equals(Input_input)){
                            i = true
                            break
                        }
                    }
                    index++
                }
                if(i){
                    // capability
                    Input input = preferenceList.get(index)
                    if (Helper.isItRightCapability(sub, input)) {
                        c = true
                    }else
                        sub.setError(true)
                }else
                    sub.setError(true)
            }

            // handler
            if(Helper.isItRightHandler(sub, methodList)) {
                h = true
            }else
                sub.setError(true)

            //make ErrorSubscribe
            if(!i || !c || !h){
                ErrorSubscribe e = new ErrorSubscribe(i, c, h, sub)
                subErrorList.add(e)
            }
        }
    }
}

