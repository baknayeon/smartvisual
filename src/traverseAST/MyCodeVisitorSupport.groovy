package traverseAST

import node.DeviceAction
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
import Setting.SettingBoxList
import support.Helper

public abstract class MyCodeVisitorSupport implements GroovyCodeVisitor {

    ArrayList preferenceList = new ArrayList()
    ArrayList subscribeList = new ArrayList()
    HashMap definition = new HashMap<>();

    HashMap dynamicMethodMap = new HashMap<>();
    HashMap ActionsCommandMap = new HashMap()
    HashMap calli2callerMap = new HashMap()

    HashMap inputList = new ArrayList()
    boolean multiPage
    boolean actionsMethodChaning
    SettingBoxList setting

    boolean preference = false
    boolean Subscribe = false
    boolean dynamicSubMethod = false
    boolean dynamicPre = false
    String actionsinMethod = null

    ArrayList<Method> dynamicPageList =  new ArrayList<Method>()
    private Stack dynamicStack = new Stack()
    private ArrayList dynamicPage = new ArrayList()
    ArrayList DynamicSubMethodList = new ArrayList()

    int Level

    private void actionMap(methodcallExpression){

        String obj = methodcallExpression.objectExpression.variable
        if (inputList.containsKey(obj)) {
            //deveice command call
            String device = obj //+"."+methodcallExpression.methodMap.value

            String commomd = methodcallExpression.method.value
            if(commomd.equals("each") || commomd.equals("eachWithIndex")){
                if(methodcallExpression.arguments)
                    if(methodcallExpression.arguments.expressions && methodcallExpression.arguments.expressions.size > 0) {
                        def colure = methodcallExpression.arguments.expressions.get(0)
                        if (colure in ClosureExpression){
                            if(colure.parameters && colure.parameters[0]){
                                def parameter = colure.parameters[0].name
                                inputList.put(parameter, device)
                            }
                        }

                    }
            }else {
                def eachDevice = inputList.get(obj)
                if(inputList.containsKey(eachDevice)){
                    device = eachDevice
                }
                if (ActionsCommandMap.containsKey(device)) {
                    DeviceAction methodsMap1 = ActionsCommandMap.get(device) ?: null;
                    methodsMap1.add(commomd, actionsinMethod)
                } else {
                    DeviceAction methodsMap1 = new DeviceAction()
                    methodsMap1.add(commomd, actionsinMethod)
                    ActionsCommandMap.put(device, methodsMap1)
                }
            }

        } else if (obj== "this") {  //methodMap call
            def method = methodcallExpression.method.value
            if (method == "runIn") {
                method = methodcallExpression.arguments.getExpressions().get(1)
                if(method in ConstantExpression)
                    method = method.value;
                else if(method in VariableExpression)
                    method = method.variable;
            }
            HashSet hashSet = calli2callerMap.get(method) ?: null
            if (hashSet) {
                hashSet.addAll(actionsinMethod)

            } else {
                HashSet newset = new HashSet();
                newset.addAll(actionsinMethod)
                calli2callerMap.put(method, newset)
            }

        }
    }
    public void visitBlockStatement(BlockStatement block) {

        if(dynamicPre)
            Level ++


        for (Statement statement : block.getStatements()) {
            if(actionsMethodChaning){
                if(statement in ExpressionStatement) {
                    def methodcallExpression = statement.expression
                    if (methodcallExpression instanceof MethodCallExpression) {
                        if (methodcallExpression.objectExpression instanceof VariableExpression
                         && methodcallExpression.method instanceof ConstantExpression)
                            actionMap(methodcallExpression)

                    }

                }
            }

            statement.visit(this);
        }


        if(dynamicPre)
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

    public void setDynamicPre(boolean t){
        dynamicPre = t
        Level = -1
    }


    public void visitMethodCallExpression(MethodCallExpression call) {


        String methodCall
        def args = (java.util.ArrayList) call.arguments.expressions
        if (call.getMethodAsString() == null)
            methodCall = call.getText()
        else
            methodCall = call.getMethodAsString()


        if (preference) {
            if (methodCall.equals("definition")){
                def arg = ((java.util.ArrayList)((TupleExpression)call.arguments).expressions).get(0)
                if(arg in MapExpression) {
                    ((MapExpression)arg).mapEntryExpressions.each { def entry ->

                        def key = ((MapEntryExpression) entry).keyExpression
                        def var = ((MapEntryExpression) entry).valueExpression
                        if(key in ConstantExpression && var in ConstantExpression) {
                            key = ((ConstantExpression)key).getText()
                            var = ((ConstantExpression)var).getText()
                            definition.put(key, var)
                        }

                    }
                }
            }else if (methodCall.equals("preferences")) {
                preferenceList.add(methodCall)
            } else if (methodCall.equals("page")) {
                multiPage = true
                //ArrayList pageArgList = (java.util.ArrayList) call.arguments.expressions

                if (Helper.isDynamicPage(args)) {
                    def dynamicPage = new Page(args, "dynamicPage")
                    dynamicMethodMap.put(dynamicPage.getName(), dynamicPage)
                    preferenceList.add(new Page(args, "dynamicPage"))
                } else {
                    preferenceList.add(new Page(args, "page"))
                }

            } else if (methodCall.equals("input")) {
                Input input = new Input(args)
                if (Helper.checkSameInputOrNot(preferenceList, input)) {
                    preferenceList.add(input)
                    inputList.put(input.getName() ,input)
                }
            } else if (methodCall.equals("label")) {
                preferenceList.add(new Label(args))

            } else if (methodCall.equals("href")) {
                preferenceList.add(new Href(args))

            } else if (methodCall.equals("section")) {
                preferenceList.add(new Section(args))
            } else if (isitinput(args)) {
                Input input = new Input(args)
                if (Helper.checkSameInputOrNot(preferenceList, input)) {
                    preferenceList.add(input)
                    inputList.put(input.getName() ,input)
                }
            }

        }else if (dynamicPre) {

            if (methodCall.equals("dynamicPage")) {
                addList(new Page(args, "dynamicPage"))
            } else if (methodCall.equals("input")) {
                addinput(args)
                dynamicSubMethod = true
            } else if (methodCall.equals("label")) {
                addList(new Label(args))
            } else if (methodCall.equals("href")) {
                addList(new Href(args))
            } else if (methodCall.equals("section")) {
                addList(new Section(args))
            }else if(isitinput(args)) {
                addinput(args)
            }else {
                if(dynamicMethodMap.containsKey(methodCall))
                    addList(new Page(methodCall, args, "methodCall"))

                if (setting.showMethod())
                    if(DynamicSubMethodList.contains(methodCall))
                            addList(new Method(methodCall))


                def method_Variable = call.objectExpression
                if (method_Variable instanceof VariableExpression && method_Variable.variable != "this" && methodCall instanceof ConstantExpression) {


                }

                if (isitinput(args)) {
                    addinput(args)
                }
            }
        }
        else if(Subscribe == false && preference == false && dynamicPre == false)
          if (methodCall.equals("input")) {
            DynamicSubMethodList.add(actionsinMethod)
        }



        if ( Subscribe && (methodCall.equals("first") || methodCall.equals("subscribeToCommand") || methodCall.equals("schedule"))) {

            if (args.size() > 0) {
                Subscribe subscribe = new Subscribe(args)
                if (Helper.checkSameSubscribeOrNot(subscribeList, subscribe)) {
                    subscribeList.add(subscribe)
                }
            }
        }

        call.getObjectExpression().visit(this);
        call.getMethod().visit(this);
        call.getArguments().visit(this);


    }

    private boolean isitinput(def args) {
        if(args.size() == 2){
            if(args.get(1) instanceof ConstantExpression) {
                def cap = args.get(1).value.toString()
                if(cap.contains("capability.") || cap.contains("mode")
                        || cap.contains("time") || cap.contains("email")
                        || cap.contains("enum") || cap.contains("number")
                        || cap.contains("password") || cap.contains("phone")
                        || cap.contains("time") || cap.contains("text")
                ){
                   return true
                }
            }
        }
        if(args.size() >= 3){
            if(args.get(2) instanceof ConstantExpression) {
                def cap = args.get(2).value
                if(cap.contains("capability.") || cap.contains("mode")
                        || cap.contains("time") || cap.contains("email")
                        || cap.contains("enum") || cap.contains("number")
                        || cap.contains("password") || cap.contains("phone")
                        || cap.contains("time") || cap.contains("text")
                ){
                    return true
                }
            }

        }

        return false

    }

    private void addinput(def args){
        Input input = new Input(args)
        addList(input)
        inputList.put(input.getName() ,input)
    }

    private void addList(def state){

        if(dynamicPre){
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
