package AST

import node.Input
import node.Method
import node.Subscribe

/**
 * Created by b_newyork on 2017-09-05.
 */


import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.GroovyClassVisitor
import org.codehaus.groovy.ast.ImportNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.PackageNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException

public abstract class MyClassCodeVisitorSupport extends MyCodeVisitorSupport implements GroovyClassVisitor {

    boolean makeingDynamicPre
    boolean dynamicPage
    ArrayList methodList

    public MyClassCodeVisitorSupport(){
        makeingDynamicPre = false
        dynamicPage = false
        methodList = new ArrayList()
    }

    public void visitMethod(MethodNode node) {
        String methodName = node.name

        setMakingPreference(false)
        setMakingDynamic(false)
        setInstalled(false)


        if ("run".equals(methodName)){
            if(!makeingDynamicPre)
                setMakingPreference(true) //first
        }

        if(!makeingDynamicPre)
            setInstalled(true)

        if(makeingDynamicPre) {//second

            for(String dynamicName : getDynamicMethodList())
                if (methodName.contains(dynamicName)) { // GString 어떻게 하지?
                    setMakingDynamic(true)
                    dynamicPage = true
                    break;
                }else
                    dynamicPage = false
            for(String method : getSubMethodList())
                if (methodName.contains(method)) { // GString 어떻게 하지?
                    setMakingDynamic(true)
                    break;
                }
        }

        if(super.error){
            HashMap inputMap = super.inputDevice_List
            ArrayList subList = super.subscribeList
            subList.each{ Subscribe sub ->
                if(inputMap.containsKey(sub.input)) {
                    inputMap.remove(sub.input)
                    //Input input = inputMap.get(sub.input)
                    //input.setUsed(true)
                    //inputMap.replace(sub.input, input)
                }
            }
        }

        visitConstructorOrMethod(node, false);

        if(!makeingDynamicPre) { //first
            if (getDummy()) {
                dynamicNameList(node.name)
                setDummy(false)
            }

            methodList.add(methodName)
        }

        if(makeingDynamicPre) { //second
            if(getMakingDynamic()) {
                addDynamicPageList(node, dynamicPage)
            }
        }
    }

    public void setDynamicPage(boolean t){
        makeingDynamicPre = t
    }

    public void setInput(boolean t){
        setError(t)
    }


    HashMap getUnused_inputDev_List() {
        return super.getInputDevice_List()
    }

    @Override
    ArrayList<Method> getDynamicPageList() {
        return super.getDynamicPageList()
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
    HashMap getDefinition() {
        return super.getDefinition()
    }

    public boolean isMultiPage(){
        return super.multiPage
    }

    public boolean isDynamicPage(){
        if(getDynamicMethodList().size() >0)
            return true
        else
            return false
    }


    public void visitClass(ClassNode node) {
        visitAnnotations(node);
        visitPackage(node.getPackage());
        visitImports(node.getModule());
        node.visitContents(this);
        visitObjectInitializerStatements(node);
    }

    protected void visitObjectInitializerStatements(ClassNode node) {
        for (Statement element : node.getObjectInitializerStatements()) {
            element.visit(this);
        }
    }

    public void visitPackage(PackageNode node) {
        if (node != null) {
            visitAnnotations(node);
            node.visit(this);
        }
    }

    public void visitImports(ModuleNode node) {
        if (node != null) {
            for (ImportNode importNode : node.getImports()) {
                visitAnnotations(importNode);
                importNode.visit(this);
            }
            for (ImportNode importStarNode : node.getStarImports()) {
                visitAnnotations(importStarNode);
                importStarNode.visit(this);
            }
            for (ImportNode importStaticNode : node.getStaticImports().values()) {
                visitAnnotations(importStaticNode);
                importStaticNode.visit(this);
            }
            for (ImportNode importStaticStarNode : node.getStaticStarImports().values()) {
                visitAnnotations(importStaticStarNode);
                importStaticStarNode.visit(this);
            }
        }
    }

    public void visitAnnotations(AnnotatedNode node) {
        List<AnnotationNode> annotations = node.getAnnotations();
        if (annotations.isEmpty()) return;
        for (AnnotationNode an : annotations) {
            // skip built-in properties
            if (an.isBuiltIn()) continue;
            for (Map.Entry<String, Expression> member : an.getMembers().entrySet()) {
                member.getValue().visit(this);
            }
        }
    }

    protected void visitClassCodeContainer(Statement code) {
        if (code != null) code.visit(this);
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        visitAnnotations(expression);
        super.visitDeclarationExpression(expression);
    }

    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        visitAnnotations(node);
        visitClassCodeContainer(node.getCode());
        for (Parameter param : node.getParameters()) {
            visitAnnotations(param);
        }
    }

    public void visitConstructor(ConstructorNode node) {
        visitConstructorOrMethod(node, true);
    }


    public void visitField(FieldNode node) {
        visitAnnotations(node);
        Expression init = node.getInitialExpression();
        if (init != null) init.visit(this);
    }

    public void visitProperty(PropertyNode node) {
        visitAnnotations(node);
        Statement statement = node.getGetterBlock();
        visitClassCodeContainer(statement);

        statement = node.getSetterBlock();
        visitClassCodeContainer(statement);

        Expression init = node.getInitialExpression();
        if (init != null) init.visit(this);
    }

    protected void addError(String msg, ASTNode expr) {
        SourceUnit source = getSourceUnit();
        source.getErrorCollector().addErrorAndContinue(
                new SyntaxErrorMessage(new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), source)
        );
    }

    protected abstract SourceUnit getSourceUnit();

    protected void visitStatement(Statement statement) {
    }

    public void visitBlockStatement(BlockStatement block) {
      //  println "visitBlockStatement"
        visitStatement(block);
        super.visitBlockStatement(block);
    }
    public void visitExpressionStatement(ExpressionStatement statement) {
       // println "visitExpressionStatement"
        visitStatement(statement);
        super.visitExpressionStatement(statement);
    }

    public void visitAssertStatement(AssertStatement statement) {
        visitStatement(statement);
        super.visitAssertStatement(statement);
    }

    public void visitBreakStatement(BreakStatement statement) {
        visitStatement(statement);
        super.visitBreakStatement(statement);
    }

    public void visitCaseStatement(CaseStatement statement) {
        visitStatement(statement);
        super.visitCaseStatement(statement);
    }

    public void visitCatchStatement(CatchStatement statement) {
        visitStatement(statement);
        super.visitCatchStatement(statement);
    }

    public void visitContinueStatement(ContinueStatement statement) {
        visitStatement(statement);
        super.visitContinueStatement(statement);
    }

    public void visitDoWhileLoop(DoWhileStatement loop) {
        visitStatement(loop);
        super.visitDoWhileLoop(loop);
    }

    public void visitForLoop(ForStatement forLoop) {
        visitStatement(forLoop);
        super.visitForLoop(forLoop);
    }

    public void visitIfElse(IfStatement ifElse) {
        //println "visitIfElse"
        visitStatement(ifElse);
        super.visitIfElse(ifElse);
    }

    public void visitReturnStatement(ReturnStatement statement) {
        visitStatement(statement);
        super.visitReturnStatement(statement);
    }

    public void visitSwitch(SwitchStatement statement) {
        visitStatement(statement);
        super.visitSwitch(statement);
    }

    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        visitStatement(statement);
        super.visitSynchronizedStatement(statement);
    }

    public void visitThrowStatement(ThrowStatement statement) {
        visitStatement(statement);
        super.visitThrowStatement(statement);
    }

    public void visitTryCatchFinally(TryCatchStatement statement) {
        visitStatement(statement);
        super.visitTryCatchFinally(statement);
    }

    public void visitWhileLoop(WhileStatement loop) {
        visitStatement(loop);
        super.visitWhileLoop(loop);
    }
}
