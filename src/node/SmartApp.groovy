package node

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import preferenceNode.Input
import preferenceNode.Page
import preferenceNode.Subscribe

/**
 * Created by b_newyork on 2018-06-08.
 */
class SmartApp {

    //first
    HashMap<String, String> definition = new HashMap<>();
    ArrayList preferenceList = new ArrayList()
    ArrayList subscribeList = new ArrayList()
    HashSet handlerMethod = new HashSet()
    HashMap subCount = new HashMap()
    HashMap<String, Input> inputMap = new HashMap()
    HashSet methodSet = new HashSet()
    HashMap<String, Page> dynamicMethodMap = new HashMap<>();

    //first action
    private int action_In_handlerMethod = 0

    //second action
    HashMap<String, String> closureInputMap = new HashMap()
    HashMap<String, DeviceAction> actionsCommandMap = new HashMap()
    HashMap<String, Integer> unsupportedCommandMap = new HashMap()
    HashMap<String, Integer> unsupportedCapMap = new HashMap()
    HashMap<String, HashSet> callGraphMap = new HashMap()
    HashMap methodMap_parameterInSend = new HashMap<>();
    HashMap methodMap_sendMethodsList = new HashMap<>();
    HashMap methodMap_setLocationMethodsList = new HashMap<>();
    HashMap methodMap_unscheduleMethodsList = new HashMap<>();

    int sendMethod = 0;
    int setLocationMethod = 0;
    int unschedule= 0;
    private HashMap variableInSendMethod = new HashMap<>();
    private HashSet sendMethodInMethod = new HashSet<>();
    private HashSet setLocationMethodInMethod = new HashSet<>();
    private HashSet unscheduleSet = new HashSet<>();

    //second dynamic
    ArrayList<Method> dynamicPageList =  new ArrayList<Method>()

    //event -> action
    HashMap event2action = new HashMap()

    void addMethodMap(String methodName) {
        this.methodSet.add(methodName)
    }
    HashMap getEvent2Action(){

        return event2action
    }
    void putDefinition(String key, String var){
        this.definition.put(key, var)
    }
    void addPreferenceList(def node){
        this.preferenceList.add(node)
    }
    void putDynamicMethodMap(String key, Page var){
        this.dynamicMethodMap.put(key, var)
    }
    void putInputMap(String key,Input var){
        this.inputMap.put(key, var)
    }

    void putEachInputMap(String each, String input) {
        closureInputMap.put(each, input)
    }
    public def isClosureInput(closure){
        return closureInputMap.containsKey(closure)
    }
    public String getEachDevice(closure){
        return closureInputMap.get(closure)
    }
    void addDynamicPageList(Method node){
        this.dynamicPageList.add(node)
    }
    void putCallGraphMap(String callee, HashSet caller){
        this.callGraphMap.put(callee, caller)
    }
    void putActionsCommandMap(String key, DeviceAction var){
            this.actionsCommandMap.put(key, var)
    }
    public boolean isitActionDevCommand(String name){
        return inputMap.containsKey(name)
    }

    public String getInputDevi(String name){
        Input input = inputMap.get(name)
        return input.getDevice()
    }

    public String getInputCap(String name){
        Input input = inputMap.get(name)
        return input.getCapability()
    }
    public ArrayList<Input> getInputs(){
        ArrayList<Input> inputArrayList = inputMap.values()
        return inputArrayList
    }
    public double getAvgLen_eventFlow (){
        double avg = 0
        double total = 0
        double count = 0
        for( String event : event2action.keySet() ){
            ArrayList methodsList = event2action.get(event)
            for(ArrayList eventFlow: methodsList){
                count++
                total += eventFlow.size()-2
            }
        }
        if(count > 0)
            avg = total/count
        else return 0

        return avg

    }

    public void setEvent2Action(event, methodList){
        if(event2action.containsKey(event)){
            ArrayList methodsList = event2action.get(event)
            methodsList.add(methodList)
        }else{
            ArrayList methodsList = new ArrayList();
            methodsList.add(methodList)
            event2action.put(event, methodsList)
        }
    }

    public int getNumof_EH(){
        HashSet set = new HashSet()
        for(Subscribe subscribe : subscribeList){

            String eh = subscribe.getHandler()
            set.add(eh)
        }
        return set.size()
    }
    public int gettheNumof_sub(){
        return subscribeList.size()
    }

    public int total_MethodFlow(){
        int num = 0;
        for(ArrayList hi : event2action.values()){
            num = num + hi.size()
        }
        return num;
    }


    public int total_ActionCommand_In_handlerMethod(){
        return action_In_handlerMethod;
    }

    public int total_actionCommand(){
        int result = 0
        ArrayList actionList = actionsCommandMap.values()
        for(DeviceAction action : actionList){
            result += action.getActionCount()
        }
        return result;
    }

    public void count_sendMethod(){
        sendMethod++
    }
    public void count_setLocationMethod(){
        setLocationMethod++
    }
    public int total_sendMethod() {
        return sendMethod;
    }
    public int total_setLocation() {
        return setLocationMethod;
    }

    public void count_unschedule() {
        unschedule++
    }
    public int total_unschedule() {
        return unschedule;
    }

    public void count_actionInEH(){
        action_In_handlerMethod++;
    }


    public ArrayList getDevice(){
        int event = 0;
        int action = 0;
        int data = 0;
        int device = 0;
        for(def input : inputMap.values()){
            String name
            if(input in Input)
                name = input.getName()
            else if( input in String)
                name = input

            if(name != null) {
                boolean isitEvent = isitEventDevice(name)
                boolean isitAction = isitActionDevice(name)

                if(isitAction || isitEvent) {
                    if (isitEvent)
                        event++
                    if (isitAction)
                        action++
                    if (isitEvent && isitAction)
                        device++
                }
                else
                    data++
            }
        }
        return [event, action, data];
    }


    public boolean isitEventDevice(String name){
        for(Subscribe sub : subscribeList){
            if(sub.getInput().equals(name))
                return true
        }
        return false
    }

    public boolean isitActionDevice(String name){
        if(actionsCommandMap.containsKey(name))
           return true
       // else if(newSet.containsKey(name))
           //return true
        return false
    }

    public void collectSendMethd(def message, String sendMethod){
        if(message in VariableExpression) {
            message = ((VariableExpression) message).variable
            if(variableInSendMethod.containsKey(message)) {
                HashSet newSet = variableInSendMethod.get(message)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                variableInSendMethod.put(message, newSet);
            }

        }
        if(message in String)
            sendMethodInMethod.add(sendMethod +"(" +message +")")
        else
            sendMethodInMethod.add(sendMethod)
    }
    public void pushSetLocation(def mode, String setLocation) {
        if(mode in VariableExpression) {
            mode = ((VariableExpression) mode).variable
        }
        setLocationMethodInMethod.add(setLocation +"(" +mode+")")
    }
    public void pushUnschedule(def arg, String unschedule) {
        if(arg in VariableExpression) {
            arg = ((VariableExpression) arg).variable
        }else if(arg in ConstantExpression) {
            arg = ((ConstantExpression) arg).value
        }
        if(arg.length() > 0)
            unscheduleSet.add(unschedule +"(" +arg+")")
        else
            unscheduleSet.add(unschedule +"()")
    }

    public void collectSendMethd(def phone, def message, String sendMethod){
        if(phone in VariableExpression) {
            phone = ((VariableExpression) phone).variable
            if(variableInSendMethod.containsKey(phone)){
                HashSet newSet = variableInSendMethod.get(phone)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                variableInSendMethod.put(phone, newSet);
            }
        }
        if(message in VariableExpression) {
            message = ((VariableExpression) message).variable
            if(variableInSendMethod.containsKey(message)) {
                HashSet newSet = variableInSendMethod.get(message)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                variableInSendMethod.put(message, newSet);
            }
        }
        if(message in String && phone in String)
            sendMethodInMethod.add(sendMethod +"(" +phone+", "+message +")")
        else
            sendMethodInMethod.add(sendMethod)

    }

    public void pushActionMethod(String methodName){
        if(variableInSendMethod.size() > 0 || sendMethodInMethod.size() > 0 || setLocationMethodInMethod.size() > 0 || unschedule > 0) {
            methodMap_parameterInSend.put(methodName, variableInSendMethod)
            methodMap_sendMethodsList.put(methodName, sendMethodInMethod)
            methodMap_setLocationMethodsList.put(methodName, setLocationMethodInMethod)
            methodMap_unscheduleMethodsList.put(methodName, unscheduleSet)

            sendMethodInMethod = new HashSet()
            variableInSendMethod = new HashMap<>()
            setLocationMethodInMethod = new HashSet<>()
            unscheduleSet = new HashSet<>()
        }
    }
    public def getVariableInSendMethod(){
        return methodMap_parameterInSend.values()
    }
    public HashMap getSendMethodByMethod(){
        return methodMap_sendMethodsList
    }
    public HashMap getSetLocaionMethodByMethod(){
        return methodMap_setLocationMethodsList
    }
    public HashMap getUnsheduleMethodByMethod(){
        return methodMap_unscheduleMethodsList
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

    private boolean checkSameInputOrNot(Input newInput){

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
        handlerMethod.add(newSubscribe.getHandler())
        subscribeList.add(newSubscribe)
        if(subCount.containsKey(newSubscribe.getInput())){
            int count = subCount.get(newSubscribe.getInput())
            count = count+1
            subCount.put(newSubscribe.getInput(), count)
        }
        else
            subCount.put(newSubscribe.getInput(), 1)
    }

    public void addUnSupportedCommand(String command){
        if(unsupportedCommandMap.containsKey(command)){
            int count = unsupportedCommandMap.get(command)
            count++
            unsupportedCommandMap.put(command, count)
        }else{
            unsupportedCommandMap.put(command, 1)
        }
    }


    public void addUnSupportedCap(String command){
        if(unsupportedCapMap.containsKey(command)){
            int count = unsupportedCapMap.get(command)
            count++
            unsupportedCapMap.put(command, count)
        }else{
            unsupportedCapMap.put(command, 1)
        }
    }


    public int getUnSupportedCommand(){
        def values = unsupportedCommandMap.values()
        int sum = 0;
        for(int count : values){
            sum += count
        }
        return sum
    }

    public int getUnSupportedCap(){
        def values = unsupportedCapMap.values()
        int sum = 0;
        for(int count : values){
            sum += count
        }
        return sum
    }

}
