package node

import javax.swing.tree.DefaultMutableTreeNode
import java.lang.reflect.Array

/**
 * Created by b_newyork on 2018-06-06.
 */
class DeviceAction {

    private HashMap commandsMap = new HashMap()
    private HashMap methodFlowMap = new HashMap()
    private HashMap methodMap = new HashMap()
    private DefaultMutableTreeNode device;

    public boolean actionMethod(String method){
        if(methodMap.containsKey(method))
            return true
        else
            return false
    }
    public void setMethodFlow(String commad, ArrayList list){
        methodFlowMap.put(commad, list)
    }

    public int getMethodFlowSize(){
        int num = 0;

        for(ArrayList handlerList :  methodFlowMap.values()){
            num = num + handlerList.size();
        }
        return num
    }

    public ArrayList getMethodFlow(String commad){
        ArrayList list =  methodFlowMap.get(commad)
        return list
    }
    public void add(String commad, String methodName){

        HashMap methodsMap1 = commandsMap.get(commad) ?: null;
        HashSet methodsMap2 = methodMap.get(methodName) ?: null;

        if (methodsMap1) {
            if (methodsMap1.containsKey(methodName)) {
                int count = methodsMap1.get(methodName)
                 count = count+1
                methodsMap1.put(methodName , count)
            } else {
                methodsMap1.put(methodName , 1)
            }
        } else {
            HashMap methodsMap12 = new HashMap()
            methodsMap12.put(methodName , 1)
            commandsMap.put(commad, methodsMap12)
        }

        if(methodsMap2){
            methodsMap2.add(commad)
        }else{
            methodsMap2 = new HashSet()
            methodsMap2.add(commad)
            methodMap.put(methodName, methodsMap2)
        }

    }
    public ArrayList getCommads(){
        return commandsMap.keySet().toArray()
    }

    public ArrayList getMethodByCommad(String commad){
        if(commandsMap.containsKey(commad)) {
            HashMap methodsMap2 = commandsMap.get(commad)

            return methodsMap2.keySet().toArray()
        }
        return null
    }

    public ArrayList getMethodList(){
        return methodMap.keySet().toArray()
    }

    public def getActionNode(DefaultMutableTreeNode inputNode, String devName){

        ArrayList methods = methodMap.keySet().toArray()
        for(String method: methods){
            DefaultMutableTreeNode methodNode
            HashSet commandset = methodMap.get(method)
            for(String command : commandset){
                methodNode = new DefaultMutableTreeNode(devName +"."+command+"()")
                methodNode.add(new DefaultMutableTreeNode(method))
                inputNode.add(methodNode)
            }
        }


    }
}
