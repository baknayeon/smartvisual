package traverseAST

import Setting.SettingBoxList
import node.DeviceAction
import node.Href
import node.Input
import node.Label
import node.Method
import node.Page
import node.Section
import node.SmartApp
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
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression;
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
import support.Helper

public abstract class MyClassCodeVisitorSupport extends MyCodeVisitorSupport implements GroovyClassVisitor {

    boolean first, second
    String methodName = null

    SmartApp smartApp = new SmartApp()
    SettingBoxList setting

    boolean preference = false

    boolean actionsMethod =false

    ///ArrayList DynamicSubMethodList = new ArrayList()


    public void visitMethod(MethodNode node) {
        methodName = node.name

        if(first) {
            if ("run".equals(methodName)) {
                preference = true
            }
        }else if(second){
            if (!"run".equals(methodName) && !"main".equals(methodName) && !"updated".equals(methodName) && !"installed".equals(methodName)){
                actionsMethod = true
            }
            if(smartApp.isitDynamicPage(methodName)){
                dynamicPre = true
            }
        }

        visitConstructorOrMethod(node, false);


        if(first) {
            if ("run".equals(methodName)) {
                preference = false
            }
        }
        else if(second) { //second
            if (!"run".equals(methodName) && !"main".equals(methodName) && !"updated".equals(methodName) && !"installed".equals(methodName)){
                actionsMethod = false
            }
            if(dynamicPre) {
                addDynamicThings(node)
                dynamicPre = false
            }
        }
    }

    SmartApp getSmartApp() {
        return smartApp
    }

    public void setSetting(SettingBoxList box) {
        this.setting = box
        super.setting = box
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        String methodCall

        ArrayList args = (java.util.ArrayList) call.arguments.expressions
        if (call.getMethodAsString() == null)
            methodCall = call.getText()
        else
            methodCall = call.getMethodAsString()

        // first
        if (first ) {
            if(methodCall.equals("subscribe") || methodCall.equals("subscribeToCommand") || methodCall.equals("schedule")){
                if (args.size() > 0) {
                    Subscribe subscribe = new Subscribe(args)
                    smartApp.addSubscribeList(subscribe)

                }
            }else if( methodCall.equals("sendPush") || methodCall.equals("sendPushMessage") ||
                    methodCall.equals("sendNotification") || methodCall.equals("sendNotificationEvent") || methodCall.equals("sendNotificationToContacts")){

                for(def arg in args){
                    if(arg in VariableExpression){
                        smartApp.addSendMethd(arg.variable, methodCall)
                    }
                }


            }else if( methodCall.equals("sendSms") || methodCall.equals("sendSmsMessage")){
                if(args.size() == 2){
                    def phone = args[0]
                    def message = args[1]
                    smartApp.addSendMethd(phone, message, methodCall)
                }

            }
        }
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
                            smartApp.definition.put(key, var)
                        }

                    }
                }
            }else if (methodCall.equals("preferences")) {
                smartApp.preferenceList.add(methodCall)
            } else if (methodCall.equals("page")) {
                if (Helper.isDynamicPage(args)) {
                    def dynamicPage = new Page(args, "dynamicPage")
                    smartApp.dynamicMethodMap.put(dynamicPage.getName(), dynamicPage)
                    smartApp.preferenceList.add(new Page(args, "dynamicPage"))
                } else {
                    smartApp.preferenceList.add(new Page(args, "page"))
                }

            } else if (methodCall.equals("input")) {
                Input input = new Input(args)
                smartApp.preferenceList.add(input)
                smartApp.inputList.put(input.getName() ,input)

            } else if (methodCall.equals("label")) {
                smartApp.preferenceList.add(new Label(args))

            } else if (methodCall.equals("href")) {
                smartApp.preferenceList.add(new Href(args))

            } else if (methodCall.equals("section")) {
                smartApp.preferenceList.add(new Section(args))
            }
        }else if (dynamicPre) {

            if (methodCall.equals("dynamicPage")) {
                addList(new Page(args, "dynamicPage"))
            } else if (methodCall.equals("label")) {
                addList(new Label(args))
            } else if (methodCall.equals("href")) {
                addList(new Href(args))
            } else if (methodCall.equals("section")) {
                addList(new Section(args))
            }else if (methodCall.equals("input")) {
                addinput(args)
               // dynamicSubMethod = true
            } /*else if(isitinput(args)) {
                addinput(args)
            } else {
                if( smartApp.dynamicMethodMap.containsKey(methodCall))
                    addList(new Page(methodCall, args, "methodCall"))

                if (setting.showMethod())
                    if(DynamicSubMethodList.contains(methodCall))
                        addList(new Method(methodCall))

                if (isitinput(args)) {
                    addinput(args)
                }
            }*/
        }else if(second && preference == false ){
            if (methodCall.equals("input")) {
                smartApp.dynamicInputMap.put(methodName, new Input(args))
            }
        }


        super.visitMethodCallExpression(call)
    }

    private void addinput(def args){
        Input input = new Input(args)
        addList(input)
        smartApp.inputList.put(input.getName() ,input)
    }

    public void addDynamicThings(MethodNode node){

        def dynamicPage = getDynamicPage()
        def dynamicStack = getDynamicStack()

        if(dynamicStack.size() > 0) {
            String methodName = node.name

            Method method = new Method(dynamicStack, dynamicPage)
            method.setMethodName(methodName)
            method.setParameter(node.getParameters())

            smartApp.dynamicPageList.add(method)
            initDynamic()
        }
        dynamicPre = false
    }

    public void BlockStatement(BlockStatement block) {

        if(dynamicPre)
            level_up()

        for (Statement statement : block.getStatements()) {
            if(actionsMethod){
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
            level_down()
    }

    private void actionMap(methodcallExpression){

        String obj = methodcallExpression.objectExpression.variable
        if (smartApp.inputList.containsKey(obj)) {
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
                                smartApp.inputList.put(parameter, device)
                            }
                        }

                    }
            }else {
                def eachDevice = smartApp.inputList.get(obj)
                if(smartApp.inputList.containsKey(eachDevice)){
                    device = eachDevice
                }
                if (smartApp.ActionsCommandMap.containsKey(device)) {
                    DeviceAction methodsMap1 = smartApp.ActionsCommandMap.get(device) ?: null;
                    methodsMap1.add(commomd, methodName)
                } else {
                    DeviceAction methodsMap1 = new DeviceAction()
                    methodsMap1.add(commomd, methodName)
                    smartApp.ActionsCommandMap.put(device, methodsMap1)
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
            HashSet hashSet = smartApp.calli2callerMap.get(method) ?: null
            if (hashSet) {
                hashSet.addAll(methodName)

            } else {
                HashSet newset = new HashSet();
                newset.addAll(methodName)
                smartApp.calli2callerMap.put(method, newset)
            }

        }
    }

    public void visitBlockStatement(BlockStatement block) {
        visitStatement(block);
        BlockStatement(block);
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

    protected void addError(String msg, ASTNode expr) {
        SourceUnit source = getSourceUnit();
        source.getErrorCollector().addErrorAndContinue(
                new SyntaxErrorMessage(new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), source)
        );
    }

    protected abstract SourceUnit getSourceUnit();

    protected void visitStatement(Statement statement) {}


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

    public void visitExpressionStatement(ExpressionStatement statement) {
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
