package node

import support.CapHelper

import javax.swing.tree.DefaultMutableTreeNode

/**
 * Created by b_newyork on 2018-06-06.
 */
class DeviceAction {

    private HashMap commandsMap = new HashMap()
    private HashMap whereMap = new HashMap()
    public String device
    public String cap
    int actionCount = 0

    public DeviceAction(String device, String cap){
        this.device = device
        this.cap = cap
    }

    public void add(String commad, String methodName){

        HashMap methodsMap1 = commandsMap.get(commad) ?: null;
        HashSet methodsMap2 = whereMap.get(methodName) ?: null;

        if (methodsMap1) {
            if (methodsMap1.containsKey(methodName)) {
                int count = methodsMap1.get(methodName)
                count = count + 1
                methodsMap1.put(methodName, count)
            } else {
                methodsMap1.put(methodName, 1)
            }
        } else {
            HashMap methodsMap12 = new HashMap()
            methodsMap12.put(methodName, 1)
            commandsMap.put(commad, methodsMap12)
        }

        if (methodsMap2) {
            methodsMap2.add(commad)
        } else {
            methodsMap2 = new HashSet()
            methodsMap2.add(commad)
            whereMap.put(methodName, methodsMap2)
        }

        actionCount++

    }
    public ArrayList getCommands(){
        return commandsMap.keySet().toArray()
    }

    public boolean isItFrequentCommands(String command){
        HashMap methods = commandsMap.get(command)
        int result
        for(Integer i :methods.values()){
            result = result + i
        }
        if(result > 1)
            return true
        else return false
    }

    public ArrayList getMethodByCommad(String commad){
        if(commandsMap.containsKey(commad)) {
            HashMap methodsMap2 = commandsMap.get(commad)

            return methodsMap2.keySet().toArray()
        }
        return null
    }

    public def getActionNode(DefaultMutableTreeNode inputNode, String devName){

        ArrayList methods = whereMap.keySet().toArray()
        for(String method: methods){
            DefaultMutableTreeNode methodNode
            HashSet commandset = whereMap.get(method)
            for(String command : commandset){
                methodNode = new DefaultMutableTreeNode(devName +"."+command+"()")
                methodNode.add(new DefaultMutableTreeNode(method))
                inputNode.add(methodNode)
            }
        }
    }
}
