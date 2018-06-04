/**
 * Created by b_newyork on 2017-08-07.
 */
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

    HashMap actiondev_methodMap
    HashMap calli2callerMap
    ArrayList  actionFlowsList = new ArrayList()
    HashMap actionDev_methodFlowsMap = new HashMap()

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
        actiondev_methodMap = codeVisitor.getActionsCommandMap()
        calli2callerMap = codeVisitor.getActionsMethodMap()

        for(String device : actiondev_methodMap.keySet()){
            def actionComandMap =  ((HashMap)actiondev_methodMap.get(device)).keySet()
            ArrayList flows = new ArrayList()

            for(String start : actionComandMap){ //deivce를 부르는 method

                actionFlow(start, new ArrayList())
                flows.addAll(actionFlowsList)
                actionFlowsList.clear()

            }
            actionDev_methodFlowsMap.put(device, flows)
        }


        if(codeVisitor.isDynamicPage() && settingList.showDynamic()){
            codeVisitor.setDynamicPage(true)
            classNode.visitContents(codeVisitor)
            dynamicPageList = codeVisitor.getDynamicPageList()
        }


        detectingError = new DetectingError(preferenceList, subscribeList, comMethodList)
        detectingError.subscribe_error()

    }

    public void actionFlow(String startingMethod, ArrayList flow){
        String method = startingMethod
        flow.add(method)

        if(calli2callerMap.containsKey(method)){
            ArrayList list = ((HashSet)calli2callerMap.get(method)).toArray()
            for( String m  : list) {
                actionFlow(m, flow)
                flow.remove(flow.size()-1)
            }
        }else {
            actionFlowsList.addAll(flow)
        }

        return
    }

    public ArrayList errorReport(){
        return detectingError.getErrorList()
    }

    public JTree getPreferenceTree(){

        makeTreetree =  new MakeTree()
        makeTreetree.setPreferList(preferenceList)
        makeTreetree.setSubscribeList(subscribeList)
        makeTreetree.setActionList(actiondev_methodMap)
        makeTreetree.setDynamicPageList(dynamicPageList)

        JTree jtree = new JTree(makeTreetree.getPage())
        jtree.setCellRenderer(new TreeCellRenderer(dynamicPageList, subscribeList, actiondev_methodMap))
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






