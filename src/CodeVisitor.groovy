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
   // ArrayList<ArrayList> FlowsList = new ArrayList<ArrayList>()
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

        generating_actions_methodFlows()

    }

    void generating_actions_methodFlows(){
        def action_methodssMap = smartAppInfo.getActionsMap()
        for(String device : action_methodssMap.keySet()){
            DeviceAction commandList = action_methodssMap.get(device)
            for(String command : commandList.getCommands()){
                ArrayList methods =  commandList.getMethodByCommad(command)
                for(String method : methods){
                    ArrayList list = new ArrayList()
                    list.add(device+"."+command)
                    actionFlow(method, list)
                }
                //commandList.setMethodFlow(command, FlowsList.clone())

                //this.FlowsList.clear()
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
                flow.add(method)
                //flow = flow.reverse()
                if(flow.size() == 1){
                    smartAppInfo.actionCommand_In_handlerMethod()
                }

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
                detectingError.addMethodError(method)

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






