package traverseAST

import Setting.SettingBoxList
import node.DeviceAction
import org.codehaus.groovy.ast.VariableScope
import preferenceNode.Href
import preferenceNode.Input
import preferenceNode.Label
import node.Method
import preferenceNode.Page
import preferenceNode.Section
import node.SmartApp
import preferenceNode.Subscribe

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
import support.CapHelper

public abstract class MyClassCodeVisitorSupport extends MyCodeVisitorSupport implements GroovyClassVisitor {

    boolean first, second
    String methodName = null

    SmartApp smartApp = new SmartApp()
    SettingBoxList setting

    boolean preference = false
    boolean actionsMethod =false


    public void visitMethod(MethodNode node) {
        methodName = node.name

        if(first) {
            if ("run".equals(methodName)) {
                preference = true
            }else {
                if (!"run".equals(methodName) && !"main".equals(methodName) && !"updated".equals(methodName) && !"installed".equals(methodName))
                   smartApp.addMethodMap(methodName)
            }
        }else if(second){
            if (!"run".equals(methodName) && !"main".equals(methodName) && !"updated".equals(methodName) && !"installed".equals(methodName)){
                actionsMethod = true
                if("stop".equals(methodName) )
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
                if("stop".equals(methodName) )
                    actionsMethod = false
            }
            smartApp.pushActionMethod(methodName)

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
                            smartApp.putDefinition(key, var)
                        }
                    }
                }
            }else if (methodCall.equals("preferences")) {
                smartApp.addPreferenceList(methodCall)
            } else if (methodCall.equals("page")) {
                if (smartApp.isDynamicPage(args)) {
                    def dynamicPage = new Page(args, "dynamicPage")
                    smartApp.putDynamicMethodMap(dynamicPage.getName(), dynamicPage)
                    smartApp.addPreferenceList(new Page(args, "dynamicPage"))
                } else {
                    smartApp.addPreferenceList(new Page(args, "page"))
                }

            } else if (methodCall.equals("input")) {
                Input input = new Input(args)
                smartApp.addPreferenceList(input)
                smartApp.putInputMap(input.getName() ,input)

            } else if (methodCall.equals("label")) {
                smartApp.addPreferenceList(new Label(args))

            } else if (methodCall.equals("href")) {
                smartApp.addPreferenceList(new Href(args))

            } else if (methodCall.equals("section")) {
                smartApp.addPreferenceList(new Section(args))
            }
        }else if (dynamicPre) {

            if (methodCall.equals("dynamicPage")) {
                addList4Dynamic(new Page(args, "dynamicPage"))
            } else if (methodCall.equals("label")) {
                addList4Dynamic(new Label(args))
            } else if (methodCall.equals("href")) {
                addList4Dynamic(new Href(args))
            } else if (methodCall.equals("section")) {
                addList4Dynamic(new Section(args))
            }else if (methodCall.equals("input")) {
                addinput4Dynamic(args)
            }
        }else if(second && preference == false ){
            if (methodCall.equals("input")) {
                //smartApp.dynamicInputMap.put(methodName, new Input(args))
            }else if( methodCall.equals("sendPush") || methodCall.equals("sendPushMessage") ||methodCall.equals("sendNotification") || methodCall.equals("sendNotificationEvent") ){

                def message = args[0]
                smartApp.collectSendMethd(message, methodCall)
                smartApp.count_sendMethod()
            }else if( methodCall.equals("sendNotificationToContacts")){

                if(args.size() >= 2){
                    def message = args[0]
                    def phone = args[1]
                    smartApp.collectSendMethd(phone, message, methodCall)
                }
                smartApp.count_sendMethod()
            }else if( methodCall.equals("sendSms") || methodCall.equals("sendSmsMessage")){
                if(args.size() == 2){
                    def phone = args[0]
                    def message = args[1]
                    smartApp.collectSendMethd(phone, message, methodCall)
                }
                smartApp.count_sendMethod()

            }
        }
        if(actionsMethod) {
            actionMap(call)
            if (methodCall.equals("setLocationMode")) {

                if(args.size() >= 1){
                    def mode = args[0]
                    smartApp.pushSetLocation(mode, methodCall)
                }
                smartApp.count_setLocationMethod()
            }else if ( methodCall.equals("unschedule")) {
                def arg
                if(args.size() >= 1){
                    arg = args[0]
                }
                smartApp.pushUnschedule(arg, methodCall)
                smartApp.count_unschedule()
            }
        }

        super.visitMethodCallExpression(call)
    }

    private void addinput4Dynamic(def args){
        Input input = new Input(args)
        addList4Dynamic(input)
        smartApp.putInputMap(input.getName() ,input)
    }

    public void addDynamicThings(MethodNode node){

        def dynamicPage = getDynamicPage()
        def dynamicStack = getDynamicStack()

        if(dynamicStack.size() > 0) {
            String methodName = node.name

            Method method = new Method(dynamicStack, dynamicPage)
            method.setMethodName(methodName)
            method.setParameter(node.getParameters())

            smartApp.addDynamicPageList(method)
            initDynamic()
        }
        dynamicPre = false
    }

    public void BlockStatement(BlockStatement block) {

        if(dynamicPre)
            level_up()
        for (Statement statement : block.getStatements()) {
            statement.visit(this);
        }

        if(dynamicPre)
            level_down()
    }

    private void actionMap(methodcallExpression){


        if (methodcallExpression instanceof MethodCallExpression)
            if (methodcallExpression.objectExpression instanceof VariableExpression
                    && methodcallExpression.method instanceof ConstantExpression) {

                String obj = methodcallExpression.objectExpression.variable
                String command = methodcallExpression.method.value
                String input = obj
                if (smartApp.isitActionDevCommand(obj)) {//deveice command call
                    if (command.equals("each") || command.equals("eachWithIndex")) {
                        if (methodcallExpression.arguments)
                            if (methodcallExpression.arguments.expressions && methodcallExpression.arguments.expressions.size > 0) {
                                def colure = methodcallExpression.arguments.expressions.get(0)
                                if (colure in ClosureExpression) {
                                    VariableScope variableScope = colure.getVariableScope()
                                    HashMap declaredVariables = variableScope.getDeclaredVariables()
                                    Set keySet = declaredVariables.keySet()
                                    for (String key : keySet) {
                                        smartApp.putEachInputMap(key, input)
                                    }
                                }

                            }
                    } else {

                        String device = smartApp.getInputDevi(input)
                        String cap = smartApp.getInputCap(input)
                        int result = CapHelper.rightCommand(cap, command)
                        if (result == 1) {
                            if (smartApp.actionsCommandMap.containsKey(input)) {
                                DeviceAction methodsMap1 = smartApp.actionsCommandMap.get(input) ?: null;
                                methodsMap1.add(command, methodName)
                            } else {
                                DeviceAction methodsMap1 = new DeviceAction(device, cap)
                                methodsMap1.add(command, methodName)
                                smartApp.putActionsCommandMap(input, methodsMap1)
                            }
                        } else if (result == 0) {
                            smartApp.addUnSupportedCommand(command)
                        }else if (result == -1) {
                            smartApp.addUnSupportedCap(cap)
                        }
                    }

                } else if (smartApp.isClosureInput(obj)) { //deveice command call with closure

                    def parentDevice = smartApp.getEachDevice(obj)
                    String device = smartApp.getInputDevi(parentDevice)
                    String cap = smartApp.getInputCap(parentDevice)

                    int result = CapHelper.rightCommand(cap, command)
                    if (result == 1) {
                        if (smartApp.actionsCommandMap.containsKey(parentDevice)) {
                            DeviceAction methodsMap1 = smartApp.actionsCommandMap.get(parentDevice) ?: null;
                            methodsMap1.add(command, methodName)
                        } else {
                            DeviceAction methodsMap1 = new DeviceAction(device, cap)
                            methodsMap1.add(command, methodName)
                            smartApp.putActionsCommandMap(parentDevice, methodsMap1)
                        }
                    } else if (result == 0) {
                        smartApp.addUnSupportedCommand(command)
                    }else if (result == -1) {
                        smartApp.addUnSupportedCap(cap)
                    }

                } else if (obj == "this") {  //methodSet call

                    String method = methodcallExpression.method.value

                    if (method == "runIn" || method == "runOnce") {
                        def arg = methodcallExpression.arguments.getExpressions().get(1)
                        if (arg in ConstantExpression) {
                            method = ((ConstantExpression) arg).getValue()
                        } else if (arg in VariableExpression)
                            method = ((VariableExpression) arg).getName()
                    } else if (method.contains("runEvery")) {
                        def arg = methodcallExpression.arguments.getExpressions().get(0)
                        if (arg in ConstantExpression) {
                            method = ((ConstantExpression) arg).getValue()
                        } else if (arg in VariableExpression)
                            method = ((VariableExpression) arg).getName()
                    }
                    HashSet hashSet = smartApp.callGraphMap.get(method) ?: null
                    if (hashSet) {
                        if (!method.equals(methodName)) //recursive
                            hashSet.addAll(methodName)

                    } else {
                        HashSet newset = new HashSet();
                        newset.addAll(methodName)
                        smartApp.putCallGraphMap(method, newset)
                    }
                }else if(CapHelper.rightCommand(command)){ // only command

                    if (smartApp.actionsCommandMap.containsKey(input)) {
                        DeviceAction methodsMap1 = smartApp.actionsCommandMap.get(input) ?: null;
                        methodsMap1.add(command, methodName)
                    } else {
                        DeviceAction methodsMap1 = new DeviceAction(input, "unknown")
                        methodsMap1.add(command, methodName)
                        smartApp.putActionsCommandMap(input, methodsMap1)
                    }
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
