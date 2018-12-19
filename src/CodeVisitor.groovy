/**
 * Created by b_newyork on 2017-08-07.
 */


import node.DeviceAction
import node.SmartApp
import preferenceNode.Subscribe
import support.CapHelper
import support.DetectingError
import support.EventFlow
import traverseAST.MyClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.transform.GroovyASTTransformation
import Setting.SettingBoxList
import support.TreeCellRenderer

import javax.swing.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class CodeVisitor extends CompilationCustomizer{
    Visualization tree
    DetectingError detectingError
    SmartApp smartAppInfo
    SettingBoxList settingList

    public CodeVisitor(SettingBoxList boxList) {
        super(CompilePhase.SEMANTIC_ANALYSIS)
        settingList = boxList
    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {

        CapHelper.loadCapRef()
        SmartThingAppCodeVisitor codeVisitor = new SmartThingAppCodeVisitor()
        codeVisitor.setSetting(settingList)

        codeVisitor.setFirst(true)
        classNode.visitContents(codeVisitor)
        codeVisitor.setFirst(false)

        smartAppInfo = codeVisitor.getSmartApp()
        codeVisitor.setSecond(true)
        classNode.visitContents(codeVisitor)
        smartAppInfo = codeVisitor.getSmartApp()
        codeVisitor.setSecond(false)


        tree = new Visualization(smartAppInfo)
        detectingError = new DetectingError(smartAppInfo)
        detectingError.subscribe_error()

        generating_eventFlows()
    }

    void generating_eventFlows(){
        def map = smartAppInfo.getActionsCommandMap()
        for(String device : map.keySet()){
            DeviceAction commandList = map.get(device)
            for(String command : commandList.getCommands()){
                ArrayList methods =  commandList.getMethodByCommad(command)
                for(String method : methods){
                    ArrayList list = new ArrayList()
                    list.add(device+"."+command)
                    actionFlow(method, list)
                }
            }
        }


        map = smartAppInfo.getSendMethodByMethod()
        for(String method : map.keySet()) {
            HashSet sendMethodSet = map.get(method)
            List<String> sendMethodList = new ArrayList<String>(sendMethodSet);
            for(String sendMethod : sendMethodList){
                ArrayList list = new ArrayList()
                list.add(sendMethod)
                actionFlow(method, list)
            }

        }
        map = smartAppInfo.getSetLocaionMethodByMethod()
        for(String method : map.keySet()) {
            HashSet setMethodSet = map.get(method)
            List<String> setMethodList = new ArrayList<String>(setMethodSet);
            for(String setMethod : setMethodList){
                ArrayList list = new ArrayList()
                list.add(setMethod)
                actionFlow(method, list)
            }

        }

        map = smartAppInfo.getUnsheduleMethodByMethod()
        for(String method : map.keySet()) {
            HashSet setMethodSet = map.get(method)
            List<String> setMethodList = new ArrayList<String>(setMethodSet);
            for(String setMethod : setMethodList){
                ArrayList list = new ArrayList()
                list.add(setMethod)
                actionFlow(method, list)
            }

        }


    }

    public void actionFlow(String startingMethod, ArrayList flow){
        String method = startingMethod
        def calli2callerMap = smartAppInfo.getCallGraphMap()
        def subscribeList = smartAppInfo.getSubscribeList()

        if(calli2callerMap.containsKey(method)){
            flow.add(method)
            ArrayList list = ((HashSet)calli2callerMap.get(method)).toArray()
            for( String m  : list) {
                actionFlow(m, flow)
                if(flow.size() > 0)
                    flow.remove(flow.size()-1)
            }
        }else{
            def result = false
            for( Subscribe entry : subscribeList){
                if(entry.handler.equals(method)){
                    result = true;
                    break;
                }
            }
            if(result){
                //flow = flow.reverse()
                if(flow.size() == 1){
                    smartAppInfo.count_actionInEH()
                }
                flow.add(method) // add event handler


                //add event
                String eventHandler = method
                for (Subscribe entry : subscribeList) {
                    if (entry.handler.equals(eventHandler)) {
                        ArrayList methods = flow.clone()
                        String event = entry.capability
                        methods.add(event)
                        Collections.reverse(methods)
                        smartAppInfo.setEvent2Action(event, methods.clone())
                        break
                    }
                }
                //FlowsList.add(flow.clone())
            }else{
                //detectingError.addMethodError(method)

            }
        }

        return
    }


    public ArrayList errorReport(){
        return detectingError.getSubErrorList()
    }

    public JTree getPreferenceTree(){

        JTree jtree = new JTree(tree.getPage())
        jtree.setCellRenderer(new TreeCellRenderer(smartAppInfo, "page") )
        jtree.setRootVisible(false)
        jtree.setShowsRootHandles(true)
        jtree.putClientProperty("JTree.lineStyle", "None")

        return jtree
    }
    public EventFlow getEventFlow(){

        EventFlow eventFlow = new EventFlow(smartAppInfo.getEvent2Action())

        return eventFlow
    }



    class SmartThingAppCodeVisitor extends MyClassCodeVisitorSupport {

        @Override
        SmartApp getSmartApp() {
            return super.getSmartApp()
        }

        @Override
        void setFirst(boolean first) {
            super.setFirst(first)
        }

        @Override
        void setSecond(boolean second) {
            super.setSecond(second)
        }
        @Override
        void setSetting(SettingBoxList setting) {
            super.setSetting(setting)
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return null
        }

    }
}






