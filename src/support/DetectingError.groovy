package support

import node.Error_sub
import node.Handler
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

            boolean subi = false
            boolean i = false
            boolean c = false
            boolean h = false

            int index  = 0

            while(index < preferenceList.size()){
                def list = preferenceList.get(index)

                if(list in Input){
                    Input input = (Input)list
                    String inputName = input.getName()

                    if(subInput.equals(inputName)){
                        subi = true
                        break
                    }
                }
                index++
            }

            if(subi){
                Input input = preferenceList.get(index)
                i = true

                if (helper.isItRightCapability(sub, input)) {
                    c = true
                }else
                    sub.setError(true)
            }else
                sub.setError(true)



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


/*    private def decoding(ArrayList leafList){

        ArrayList list = new ArrayList()

        leafList.each { DefaultMutableTreeNode node ->
            String type = node.getUserObject().toString().split()[0]
            String name = node.getUserObject().toString().split()[1]
            String cap = node.getChildAt(0)?.toString()
            ArrayList handler = makeHandlerList(node)


            if(type.equals("input")) {
                Input input = new Input()
                input.setName(name)
                input.setCapability(cap)
                input.setHandler(handler)
                list.add(input)
            }else if(type.equals("href")){

            }else if(type.equals("label")){
            }

        }

        return list
    }
    private def makeHandlerList(DefaultMutableTreeNode node) {
        ArrayList handlerList = new ArrayList()

        int i = 1
        while (i < node.getChildCount()) {
            DefaultMutableTreeNode handlerNode = node.getChildAt(i++)
            String handlerText = handlerNode.getUserObject().toString()
            String cap = handlerNode.getChildAt(0)
            Handler handler = new Handler(handlerText, cap)

            handlerList.add(handler)
        }

        return handlerList
    }*/


}

