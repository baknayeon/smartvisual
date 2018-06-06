/**
 * Created by b_newyork on 2017-08-07.
 */

import node.Subscribe
import support.DetectingError
import traverseAST.MyClassCodeVisitorSupport
import node.Method
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.transform.GroovyASTTransformation
import support.Helper

import Setting.SettingBoxList
import support.TreeCellRenderer

import javax.swing.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class CodeVisitor extends CompilationCustomizer{

    MakeTree makeTreetree
    DetectingError detectingError

    HashMap definition
    ArrayList preferenceList
    ArrayList subscribeList
    ArrayList dynamicPageList
    HashMap comMethodList

    HashMap action_methodssMap
    HashMap calli2callerMap
    ArrayList<ArrayList>  actionFlowsList = new ArrayList<ArrayList>()
    HashMap action_methodFlowsssMap = new HashMap()

    SettingBoxList settingList
    boolean multiPage

    public CodeVisitor(SettingBoxList boxList) {
        super(CompilePhase.SEMANTIC_ANALYSIS)

        settingList = boxList
        multiPage = false
    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        Helper.loadCapRef()

        SmartThingAppCodeVisitor codeVisitor = new SmartThingAppCodeVisitor()
        codeVisitor.setSetting(settingList)

        codeVisitor.setMakingPre(true)
        classNode.visitContents(codeVisitor)
        codeVisitor.setMakingPre(false)

        preferenceList = codeVisitor.getPreferenceList()
        subscribeList = codeVisitor.getSubscribeList()
        multiPage = codeVisitor.getMultiPage()
        comMethodList = codeVisitor.getCommonMethodList()
        definition = codeVisitor.getDefinition()
        action_methodssMap = codeVisitor.getActionsCommandMap()
        calli2callerMap = codeVisitor.getActionsMethodMap()

        if(codeVisitor.isDynamicPage() && settingList.showDynamic()){
            codeVisitor.setDynamicPage(true)
            classNode.visitContents(codeVisitor)
            dynamicPageList = codeVisitor.getDynamicPageList()
        }


        detectingError = new DetectingError(preferenceList, subscribeList, comMethodList)
        detectingError.subscribe_error()


        generating_actions_methodFlows()

    }

    void generating_actions_methodFlows(){
        for(String device : action_methodssMap.keySet()){
            def actionComandMethodList =  ((HashMap)action_methodssMap.get(device)).keySet()
            ArrayList actionComandList=  ((HashMap)action_methodssMap.get(device)).values()
            actionComandMethodList.eachWithIndex{ String start , int i -> //deivce를 부르는 method
                ArrayList flow = new ArrayList()
                String Command = ((HashSet)((ArrayList)actionComandList.get(i)).get(1)).toArray()[0]//((String)((HashSet)((ArrayList)actionComandList.get(i)).get(1)).toArray()[0]).value[0]
                flow.add(Command)
                actionFlow(start, flow)
            }
            action_methodFlowsssMap.put(device, actionFlowsList.clone())
            actionFlowsList.clear()
        }
    }

    public void actionFlow(String startingMethod, ArrayList flow){
        String method = startingMethod

        if(calli2callerMap.containsKey(method)){
            flow.add(method)
            ArrayList list = ((HashSet)calli2callerMap.get(method)).toArray()
            for( String m  : list) {
                actionFlow(m, flow)
                flow.remove(flow.size()-1)
            }
        }else {
            def result = false
            for( Subscribe entry : subscribeList){
                if(entry.handler.equals(method)){
                    result = true;
                    break;
                }
            }
            if(result){
                flow.add(method)
                actionFlowsList.add(flow.clone())
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

        makeTreetree =  new MakeTree()
        makeTreetree.setPreferList(preferenceList)
        makeTreetree.setSubscribeList(subscribeList)
        makeTreetree.setAction_methodssMap(action_methodssMap)
        makeTreetree.setDynamicPageList(dynamicPageList)

        JTree jtree = new JTree(makeTreetree.getPage())
        jtree.setCellRenderer(new TreeCellRenderer(dynamicPageList, subscribeList, action_methodssMap))
        jtree.setRootVisible(false)
        jtree.setShowsRootHandles(true)
        jtree.putClientProperty("JTree.lineStyle", "None")

        return jtree
    }
    public JTree getActionTree(){

        if(makeTreetree == null) {
            makeTreetree = new MakeTree()
            makeTreetree.setAction_methodssMap(action_methodssMap)
        }

        JTree jtree = new JTree(makeTreetree.getAction(action_methodFlowsssMap))
        jtree.setCellRenderer(new TreeCellRenderer(action_methodFlowsssMap))
        jtree.setRootVisible(false)
        jtree.setShowsRootHandles(true)
        jtree.putClientProperty("JTree.lineStyle", "None")

        return jtree
    }

    class SmartThingAppCodeVisitor extends MyClassCodeVisitorSupport {


        void setDynamicPage(boolean b){
            super.setDynamicPage(b)
        }

        void setMakingPre(boolean b) {
            super.setMakeingPre(b)
        }

        @Override
        ArrayList getPreferenceList() {
            return super.getPreferenceList()
        }

        @Override
        ArrayList getSubscribeList() {
            return super.getSubscribeList()
        }

        @Override
        ArrayList<Method> getDynamicPageList() {
            return super.getDynamicPageList()
        }

        @Override
        boolean isMultiPage() {
            return super.isMultiPage()
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return null
        }

        @Override
        boolean isDynamicPage() {
            return super.isDynamicPage()
        }

        @Override
        void setSetting(SettingBoxList setting) {
            super.setSetting(setting)
        }

        @Override
        HashMap getDefinition() {
            return super.getDefinition()
        }
    }
}






