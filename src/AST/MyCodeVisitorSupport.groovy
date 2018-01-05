package AST

import node.Method
import node.Href
import node.Input
import node.Label
import node.Page
import node.Section
import node.Subscribe

/**
 * Created by b_newyork on 2017-09-05.
 */
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.expr.AttributeExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ClosureListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.FieldExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.MethodPointerExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.PostfixExpression
import org.codehaus.groovy.ast.expr.PrefixExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.RangeExpression
import org.codehaus.groovy.ast.expr.SpreadExpression
import org.codehaus.groovy.ast.expr.SpreadMapExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.UnaryPlusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.BytecodeExpression
import support.CheckBoxList
import support.Helper

public abstract class MyCodeVisitorSupport implements GroovyCodeVisitor {

    ArrayList preferenceList = new ArrayList()
    ArrayList subscribeList = new ArrayList()

    boolean multiPage

    ArrayList<Method> dynamicPageList =  new ArrayList<Method>()
    private Stack dynamicStack = new Stack()
    private ArrayList dynamicPage = new ArrayList()

    ArrayList dynamicMethodList = new ArrayList()
    ArrayList subMethodList = new ArrayList()

    boolean makingPreference = false
    boolean makingDynamic = false
    boolean installed = false
    boolean dummy = false

    int Level

    Helper helper = new Helper()
    CheckBoxList setting

    public void visitBlockStatement(BlockStatement block) {

        if(makingDynamic)
            Level ++

        for (Statement statement : block.getStatements()) {
            statement.visit(this);
        }

        if(makingDynamic)
            Level--

    }

    public void addDynamicPageList(MethodNode node, boolean dynamicType){
        if(dynamicStack.size() > 0) {
            String methodName = node.name

            Method method = new Method(dynamicStack, dynamicPage)
            method.setMethodName(methodName)
            method.setParameter(node.getParameters())
            method.setDynamic(dynamicType)

            dynamicPageList.add(method)
            dynamicStack = new Stack()
            dynamicPage = new ArrayList()
        }
    }

    public void setMakingDynamic(boolean t){
        makingDynamic = t
        Level = -1
    }

    public void dynamicNameList(String name){
        if(!dynamicMethodList.contains(name))
            subMethodList.add(name)
    }

    public void visitMethodCallExpression(MethodCallExpression call) {


        String methodCall
        //def args = call.arguments
        def args = (java.util.ArrayList) call.arguments.expressions
        if (call.getMethodAsString() == null)
            methodCall = call.getText()
        else
            methodCall = call.getMethodAsString()


        if (makingPreference) {
            if (methodCall.equals("preferences")) {
                preferenceList.add(methodCall)
            } else if (methodCall.equals("page")) {
                multiPage = true
                //ArrayList pageArgList = (java.util.ArrayList) call.arguments.expressions

                if (helper.isDynamicPage(args)) {
                    dynamicMethodList.add(new Page(args, "dynamicPage").getName())
                    preferenceList.add(new Page(args, "dynamicPage"))
                } else {
                    preferenceList.add(new Page(args, "page"))
                }

            } else if (methodCall.equals("input")) {
                Input input = new Input(args)
                if (helper.checkSameInputOrNot(preferenceList, input)) {
                    preferenceList.add(input)
                }
            } else if (methodCall.equals("label")) {
                preferenceList.add(new Label(args))

            } else if (methodCall.equals("href")) {
                preferenceList.add(new Href(args))

            } else if (methodCall.equals("section")) {
                preferenceList.add(new Section(args))
            }
        } else if (makingDynamic) {

            if (methodCall.equals("dynamicPage")) {
                addList(new Page(args, "dynamicPage"))
            } else if (methodCall.equals("input")) {
                Input input = new Input(args)
                addList(input)
            } else if (methodCall.equals("label")) {
                addList(new Label(args))
            } else if (methodCall.equals("href")) {
                addList(new Href(args))
            } else if (methodCall.equals("section")) {
                addList(new Section(args))
            } else {
                for (String dynamicNode : dynamicMethodList) {
                    if (methodCall.equals(dynamicNode)) {
                        addList(new Page(dynamicNode, args, "methodCall"))
                        break
                    }
                }
                if (setting.showMethod())
                    for (String node : subMethodList) {
                        if (methodCall.equals(node)) {
                            addList(new Method(node, args))
                            break
                        }
                    }
            }
        } else {

            if (methodCall.equals("input") || methodCall.equals("href") || methodCall.equals("label")) {
                dummy = true
            }

        }

        if (installed)
            if (methodCall.equals("subscribe") || methodCall.equals("subscribeToCommand")) {

                if (args.size() > 0) {
                    Subscribe subscribe = new Subscribe(args)
                    //if (helper.checkSameSubscribeOrNot(subscribeList, subscribe)) {
                        subscribeList.add(subscribe)
                    //}
                }
            }


        call.getObjectExpression().visit(this);
        call.getMethod().visit(this);
        call.getArguments().visit(this);
    }


    private void addList(def state){

        if(makingDynamic){
            dynamicStack.push(Level)
            dynamicPage.add(state)
        }
    }

    public void visitIfElse(IfStatement ifElse) {

        if(setting.showIf())
            addList("if")

        ifElse.getBooleanExpression().visit(this);
        ifElse.getIfBlock().visit(this);

        Statement elseBlock = ifElse.getElseBlock();
        if (elseBlock instanceof EmptyStatement) {
            // dispatching to EmptyStatement will not call back visitor,
            // must call our visitEmptyStatement explicitly
            visitEmptyStatement((EmptyStatement) elseBlock);
        } else {
            elseBlock.visit(this);
        }
    }

    public void visitExpressionStatement(ExpressionStatement statement) {
        statement.getExpression().visit(this);
    }

    public void visitForLoop(ForStatement forLoop) {
        addList("for")
        forLoop.getCollectionExpression().visit(this);
        forLoop.getLoopBlock().visit(this);
    }

    public void visitWhileLoop(WhileStatement loop) {
        if(setting.showWhile())
            addList("while")
        loop.getBooleanExpression().visit(this);
        loop.getLoopBlock().visit(this);
    }

    public void visitDoWhileLoop(DoWhileStatement loop) {

        addList("doWhile")

        loop.getLoopBlock().visit(this);
        loop.getBooleanExpression().visit(this);
    }

    public void visitReturnStatement(ReturnStatement statement) {

        addList("return")
        statement.getExpression().visit(this);
    }

    public void visitAssertStatement(AssertStatement statement) {
        statement.getBooleanExpression().visit(this);
        statement.getMessageExpression().visit(this);
    }

    public void visitTryCatchFinally(TryCatchStatement statement) {

        addList("tryCatch")

        statement.getTryStatement().visit(this);
        for (CatchStatement catchStatement : statement.getCatchStatements()) {
            catchStatement.visit(this);
        }
        Statement finallyStatement = statement.getFinallyStatement();
        if (finallyStatement instanceof EmptyStatement) {
            // dispatching to EmptyStatement will not call back visitor,
            // must call our visitEmptyStatement explicitly
            visitEmptyStatement((EmptyStatement) finallyStatement);
        } else {
            finallyStatement.visit(this);
        }
    }

    protected void visitEmptyStatement(EmptyStatement statement) {
        // noop
    }

    public void visitSwitch(SwitchStatement statement) {

        addList("Switch")

        statement.getExpression().visit(this);
        for (CaseStatement caseStatement : statement.getCaseStatements()) {
            caseStatement.visit(this);
        }
        statement.getDefaultStatement().visit(this);
    }

    public void visitCaseStatement(CaseStatement statement) {

        addList("Case")
        statement.getExpression().visit(this);
        statement.getCode().visit(this);
    }

    public void visitBreakStatement(BreakStatement statement) {
        addList("Break")
    }

    public void visitContinueStatement(ContinueStatement statement) {
    }

    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        statement.getExpression().visit(this);
        statement.getCode().visit(this);
    }

    public void visitThrowStatement(ThrowStatement statement) {
        statement.getExpression().visit(this);
    }

    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {

        call.getArguments().visit(this);
    }

    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        call.getArguments().visit(this);
    }

    public void visitBinaryExpression(BinaryExpression expression) {
        expression.getLeftExpression().visit(this);
        expression.getRightExpression().visit(this);
    }

    public void visitTernaryExpression(TernaryExpression expression) {
        expression.getBooleanExpression().visit(this);
        expression.getTrueExpression().visit(this);
        expression.getFalseExpression().visit(this);
    }

    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        visitTernaryExpression(expression);
    }

    public void visitPostfixExpression(PostfixExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitPrefixExpression(PrefixExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitBooleanExpression(BooleanExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitNotExpression(NotExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitClosureExpression(ClosureExpression expression) {

        expression.getCode().visit(this);
    }

    public void visitTupleExpression(TupleExpression expression) {
        visitListOfExpressions(expression.getExpressions());
    }

    public void visitListExpression(ListExpression expression) {
        visitListOfExpressions(expression.getExpressions());
    }

    public void visitArrayExpression(ArrayExpression expression) {
        visitListOfExpressions(expression.getExpressions());
        visitListOfExpressions(expression.getSizeExpression());
    }

    public void visitMapExpression(MapExpression expression) {
        visitListOfExpressions(expression.getMapEntryExpressions());

    }

    public void visitMapEntryExpression(MapEntryExpression expression) {
        expression.getKeyExpression().visit(this);
        expression.getValueExpression().visit(this);

    }

    public void visitRangeExpression(RangeExpression expression) {
        expression.getFrom().visit(this);
        expression.getTo().visit(this);
    }

    public void visitSpreadExpression(SpreadExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        expression
        expression.getExpression().visit(this);
        expression.getMethodName().visit(this);
    }

    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitCastExpression(CastExpression expression) {
        expression.getExpression().visit(this);
    }

    public void visitConstantExpression(ConstantExpression expression) {
    }

    public void visitClassExpression(ClassExpression expression) {
    }

    public void visitVariableExpression(VariableExpression expression) {
    }

    public void visitDeclarationExpression(DeclarationExpression expression) {
        visitBinaryExpression(expression);
    }

    public void visitPropertyExpression(PropertyExpression expression) {
        expression.getObjectExpression().visit(this);
        expression.getProperty().visit(this);
    }

    public void visitAttributeExpression(AttributeExpression expression) {
        expression.getObjectExpression().visit(this);
        expression.getProperty().visit(this);
    }

    public void visitFieldExpression(FieldExpression expression) {
    }

    public void visitGStringExpression(GStringExpression expression) {
        visitListOfExpressions(expression.getStrings());
        visitListOfExpressions(expression.getValues());
    }

    protected void visitListOfExpressions(List<? extends Expression> list) {
        if (list == null) return;
        for (Expression expression : list) {
            if (expression instanceof SpreadExpression) {
                Expression spread = ((SpreadExpression) expression).getExpression();
                spread.visit(this);
            } else {
                expression.visit(this);
            }
        }
    }

    public void visitCatchStatement(CatchStatement statement) {
        statement.getCode().visit(this);
    }

    public void visitArgumentlistExpression(ArgumentListExpression ale) {
        visitTupleExpression(ale);
    }

    public void visitClosureListExpression(ClosureListExpression cle) {
        visitListOfExpressions(cle.getExpressions());
    }

    public void visitBytecodeExpression(BytecodeExpression cle) {
    }

}
