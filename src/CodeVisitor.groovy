/**
 * Created by b_newyork on 2017-08-07.
 */

import node.Input
import support.DetectingError
import AST.MyClassCodeVisitorSupport
import node.Method
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.transform.GroovyASTTransformation
import support.Helper
import support.Logger
import support.SettingBoxList
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
    ArrayList methodList
    HashMap unused_inputDev_List


    SettingBoxList settingList
    boolean multiPage

    Logger log

    public CodeVisitor(SettingBoxList boxList) {
        super(CompilePhase.SEMANTIC_ANALYSIS)

        settingList = boxList
        multiPage = false
        log = new Logger("." + "/" + "unusedInput.txt");

    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        Helper.loadCapRef()


        SmartThingAppCodeVisitor codeVisitor = new SmartThingAppCodeVisitor()
        codeVisitor.setSetting(settingList)

        classNode.visitContents(codeVisitor)

        preferenceList = codeVisitor.getPreferenceList()
        subscribeList = codeVisitor.getSubscribeList()
        multiPage = codeVisitor.getMultiPage()
        methodList = codeVisitor.getMethodList()
        definition = codeVisitor.getDefinition()

        log.append("============================")
        log.append(definition.get("name").toString())

        if(codeVisitor.isDynamicPage() && settingList.showDynamic()){
            codeVisitor.setDynamicPage(true)
            classNode.visitContents(codeVisitor)
            dynamicPageList = codeVisitor.getDynamicPageList()
        }

        codeVisitor.setInput(true)
        classNode.visitContents(codeVisitor)
        unused_inputDev_List = codeVisitor.getUnused_inputDev_List()
        unused_inputDev_List.eachWithIndex{ def entry, int i ->
            log.append("#"+ i+" "+"unused dev")
            Input input = entry.getValue()
            log.append("\tname: "+input.name)
            log.append("\tdevice: "+input.device)
            log.append("\tcap: "+input.capability)
        }

        detectingError = new DetectingError(preferenceList, subscribeList,  methodList)
        detectingError.subscribe_error()

    }

    public ArrayList errorReport(){
        return detectingError.getErrorList()
    }

    public JTree getPreferenceTree(){

        makeTreetree =  new MakeTree()
        makeTreetree.setPreferList(preferenceList)
        makeTreetree.setSubscribeList(subscribeList)
        makeTreetree.setDynamicPageList(dynamicPageList)

        JTree jtree = new JTree(makeTreetree.getPage())
        jtree.setCellRenderer(new TreeCellRenderer(dynamicPageList, subscribeList))
        jtree.setRootVisible(false)
        jtree.setShowsRootHandles(true)
        jtree.putClientProperty("JTree.lineStyle", "None")

        return jtree
    }

    class SmartThingAppCodeVisitor extends MyClassCodeVisitorSupport {

        public void setInput(boolean b){
            super.setInput(b)
        }

        public void setDynamicPage(boolean b){
            super.setDynamicPage(b)
        }

        @Override
        HashMap getInputDevice_List() {
            return super.getInputDevice_List()
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
        ArrayList getMethodList() {
            ArrayList list = super.getMethodList()
            list.remove("main")
            list.remove("run")
            list.remove("installed")
            list.remove("updated")

            return list
        }

        @Override
        HashMap getDefinition() {
            return super.getDefinition()
        }
    }
}






