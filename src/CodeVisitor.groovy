/**
 * Created by b_newyork on 2017-08-07.
 */
import support.DetectingError
import AST.MyClassCodeVisitorSupport
import node.Method
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.transform.GroovyASTTransformation
import support.CheckBoxList
import support.TreeCellRenderer

import javax.swing.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class CodeVisitor extends CompilationCustomizer{

    MakeTree makeTreetree
    DetectingError detectingError

    ArrayList preferenceList
    ArrayList subscribeList
    ArrayList dynamicPageList
    ArrayList methodList


    CheckBoxList settingList
    boolean multiPage

    public CodeVisitor(CheckBoxList boxList) {
        super(CompilePhase.SEMANTIC_ANALYSIS)

        settingList = boxList
        multiPage = false

    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {

        SmartThingAppCodeVisitor codeVisitor = new SmartThingAppCodeVisitor()
        codeVisitor.setSetting(settingList)

        classNode.visitContents(codeVisitor)

        preferenceList = codeVisitor.getPreferenceList()
        subscribeList = codeVisitor.getSubscribeList()
        multiPage = codeVisitor.getMultiPage()
        methodList = codeVisitor.getMethodList()

        if(codeVisitor.isDynamicPage() && settingList.showDynamic()){
            codeVisitor.makeDynamicPage(true)
            classNode.visitContents(codeVisitor)
            dynamicPageList = codeVisitor.getDynamicPageList()
        }

        detectingError = new DetectingError(preferenceList, subscribeList,  methodList)
        detectingError.run()

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


        public void makeDynamicPage(boolean b){
            super.makeDynamicPage(b)
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
        void setSetting(CheckBoxList setting) {
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
    }
}






