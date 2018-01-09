package support

import node.Error_sub
import node.Input
import node.Subscribe

import javax.swing.tree.DefaultMutableTreeNode

/**
 * Created by b_newyork on 2017-12-05.
 */
class DetectingError {

    private ArrayList preferenceList
    private ArrayList subscribeList
    private ArrayList methodList

    ArrayList errorList

    public DetectingError(ArrayList pre, ArrayList sub, ArrayList method) {
        preferenceList = pre
        subscribeList = sub
        methodList = method
    }

    public void run(){

        errorList = new ArrayList<>()
        error()
    }

    private void error() {
        Helper helper = new Helper()

        subscribeList.each { Subscribe sub ->
            String subInput = sub.getInput()

            boolean sub_i = false
            boolean i = false
            boolean c = false
            boolean h = false

            int index  = 0

            if(subInput.equals("location") || subInput.equals("app")){
                i = true
                String sub_cap = sub.getCapability()
                if(sub_cap.equals("position") || sub_cap.equals("sunriseTime") || sub_cap.equals("sunsetTime") )
                        c = true
                else
                    sub.setError(true)
            }else{
                while(index < preferenceList.size()){
                    def list = preferenceList.get(index)

                    if(list in Input){
                        Input input = (Input)list
                        String inputName = input.getName()

                        if(subInput.equals(inputName)){
                            sub_i = true
                            break
                        }
                    }
                    index++
                }
                if(sub_i){
                    Input input = preferenceList.get(index)
                    i = true

                    if (helper.isItRightCapability(sub, input)) {
                        c = true
                    }else
                        sub.setError(true)
                }else
                    sub.setError(true)
            }

            if(helper.isItRightHandler(sub, methodList)) {
                h = true
            }else
                sub.setError(true)



            if(!i || !c || !h){
                Error_sub e = new Error_sub(i, c, h, sub)
                errorList.add(e)
            }

        }
    }
}

