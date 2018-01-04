package support

import node.Capability
import node.Input
import node.Section
import node.Subscribe
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Created by b_newyork on 2017-09-01.
 */
class Helper {

    String methodCall= ""
    Map allCommands

    public Helper(){
        allCommands = new HashMap()

        loadCapRef()
    }

    private def loadCapRef() {

        def capsAsPerSamsungFile = "./" + "Capabilities.csv"
        def file = new File(capsAsPerSamsungFile)

        file.splitEachLine(",") { fields ->

            Capability cap =  new Capability()

            String Cap = fields[1]?.toString()
            cap.setCapability(Cap)

            String dev = fields[2]?.toString()
            cap.setDevice(dev)

            fields[3]?.split(" ")?.each {
                cap.cap_val.add(it.toString())
            }

            allCommands[Cap] =  cap
        }
    }

    private boolean isItRightCapability(Subscribe sub, Input input){

        String sub_capVal = sub.getCap_val().get(1)

        String input_cap = input.getCapability()
        Capability cap = allCommands[input_cap]

        if(cap !=null){
            if(cap.checkVal(sub_capVal))
                return true
            else
                return false
        }
        return false
    }

    private boolean isItRightHandler(Subscribe sub, ArrayList methodList){

        for(methodName in methodList){
            if(methodName.equals(sub.getHandler().toString()))
                return true
        }
        return false
    }

    boolean isDynamicPage(ArrayList pageArgList){

        if( pageArgList.size() == 1)
            return true
        else
            return false

    }

    boolean checkSameInputOrNot(ArrayList preferenList, Input newInput){

        for(entry in preferenList)
            if(entry instanceof Input){
                Input input = entry
                ArrayList list = input.getOption();

                if (input.getName().equals(newInput.getName()))
                    return false
                else if(list.size() > 0){
                    if(checkSameInputOrNot(list ,newInput)){

                    }else{
                        return false
                    }
                }
            }

        return true
    }

    boolean checkSameSubscribeOrNot(ArrayList subscribeList, Subscribe newSubscribe){

        for(entry in subscribeList){
            if(entry instanceof Subscribe){
                Subscribe subscribe = entry
                if (subscribe.getInput().equals(newSubscribe.getInput()))
                    if (subscribe.getHandler().equals(newSubscribe.getHandler()))
                        if (subscribe.getCapability().equals(newSubscribe.getCapability()))
                            return false
            }
        }

        return true
    }

    private void handingArgs(ConstantExpression arg, ArrayList listArg){
        def constant = arg.getText()
        listArg.add(constant)
    }

    private void handingArgs(PropertyExpression arg, ArrayList listArg){
        def property = ((PropertyExpression) arg).getText()
        listArg.add(property)
    }

    private void handingArgs(VariableExpression argvex, ArrayList listArg){
        def varName = argvex.getName()
        listArg.add(varName)
    }

    private void handingArgs(GStringExpression arg, ArrayList listArg){
        listArg.add(arg.verbatimText.toString())
    }

    private void handingArgs(MapExpression arg, ArrayList listArg){

        ArrayList shouldBeFront = new ArrayList()
        arg?.mapEntryExpressions.each{ inner ->

            def keyExpr = inner.getKeyExpression()
            def valExpr = inner.getValueExpression()

            if (keyExpr instanceof ConstantExpression) {
                def keytxt = ((ConstantExpression) keyExpr).getText()


                def sub = new ArrayList();
                if(valExpr instanceof ConstantExpression){
                    ConstantExpression cap_valexp = (ConstantExpression) valExpr
                    def valtxt = cap_valexp.getText()

                    if(methodCall.equals("href")){
                        if(keytxt.equals("Page") ) {
                            shouldBeFront.add(valtxt)
                        }
                    }else {
                        if (keytxt.equals("title") || keytxt.equals("type")) {
                            shouldBeFront.add(valtxt)
                        } else {
                            sub.add(keytxt)
                            sub.add(valtxt)
                            listArg.add(sub)
                        }
                    }

                }else if(valExpr instanceof GStringExpression){
                    def valtxt = ((ConstantExpression)((java.util.ArrayList)((GStringExpression)valExpr).strings).get(0)).value
                    if(keytxt.equals("title") || keytxt.equals("type")) {
                        shouldBeFront.add(valtxt)
                    }
                }else if(valExpr instanceof ListExpression){
                    def arrayList = (java.util.ArrayList) ((ListExpression) valExpr).expressions
                    def valList = new ArrayList();

                    arrayList.eachWithIndex{ def entry, int i ->
                        if (entry instanceof ConstantExpression) {
                            valList.add(entry.value)
                        } else if (entry instanceof MapExpression) {
                            MapExpression mex = (MapExpression) entry
                            def val_mex_keytxt
                            def val_mex_valtxt

                            mex?.mapEntryExpressions.each { subinner ->
                                val_mex_keytxt = ((ConstantExpression) subinner.getKeyExpression()).getText()
                                val_mex_valtxt = ((ConstantExpression) subinner.getValueExpression()).getText()

                                def subv = new ArrayList();
                                subv.add(val_mex_keytxt)
                                subv.add(val_mex_valtxt)

                                valList.(subv)
                            }
                        }
                    }

                    sub.add(keytxt)
                    sub.add(valList)
                    listArg.add(sub)
                }
            }
        }
        listArg.addAll(shouldBeFront)
    }

    private void handingArgs(ClosureExpression args, ArrayList inputArg){
        if(methodCall.equals("input")) {
            ((java.util.ArrayList)((BlockStatement)args.code).statements).eachWithIndex{ def entry, int i ->
                handingClosureInputArgs(entry,inputArg)
            }
        }
    }

    private void handingClosureInputArgs(ExpressionStatement args, ArrayList inputArg){
        ArrayList ClosureInputArgs = new ArrayList()
        ((java.util.ArrayList)(((MethodCallExpression)args.expression).arguments).expressions).eachWithIndex{ def entry, int i ->
            handingArgs(entry , ClosureInputArgs)
        }
        ClosureInputArgs.add("input")
        ClosureInputArgs = ClosureInputArgs.reverse()
        inputArg.add(0,ClosureInputArgs)
    }

    private void handingArgs(NamedArgumentListExpression arg1, ArrayList listArg){

        NamedArgumentListExpression arg = (NamedArgumentListExpression) arg1
        ArrayList shouldBeFront = new ArrayList()
        arg?.mapEntryExpressions.each{ inner ->

            def keyExpr = inner.getKeyExpression()
            def valExpr = inner.getValueExpression()

            if (keyExpr instanceof ConstantExpression) {
                def keytxt = ((ConstantExpression) keyExpr).getText()


                def sub = new ArrayList();
                if(valExpr instanceof ConstantExpression){
                    ConstantExpression cap_valexp = (ConstantExpression) valExpr
                    def valtxt = cap_valexp.getText()

                    if(methodCall.equals("href")){
                        if(keytxt.equals("Page") ) {
                            shouldBeFront.add(valtxt)
                        }
                    }else {
                        if (keytxt.equals("title") || keytxt.equals("type")) {
                            shouldBeFront.add(valtxt)
                        } else {
                            sub.add(keytxt)
                            sub.add(valtxt)
                            listArg.add(sub)
                        }
                    }

                }else if(valExpr instanceof GStringExpression){
                    def valtxt = ((ConstantExpression)((java.util.ArrayList)((GStringExpression)valExpr).strings).get(0)).value
                    if(keytxt.equals("title") || keytxt.equals("type")) {
                        shouldBeFront.add(valtxt)
                    }
                }else if(valExpr instanceof ListExpression){
                    def arrayList = (java.util.ArrayList) ((ListExpression) valExpr).expressions
                    def valList = new ArrayList();

                    arrayList.eachWithIndex{ def entry, int i ->
                        if (entry instanceof ConstantExpression) {
                            valList.add(entry.value)
                        } else if (entry instanceof MapExpression) {
                            MapExpression mex = (MapExpression) entry
                            def val_mex_keytxt
                            def val_mex_valtxt

                            mex?.mapEntryExpressions.each { subinner ->
                                val_mex_keytxt = ((ConstantExpression) subinner.getKeyExpression()).getText()
                                val_mex_valtxt = ((ConstantExpression) subinner.getValueExpression()).getText()

                                def subv = new ArrayList();
                                subv.add(val_mex_keytxt)
                                subv.add(val_mex_valtxt)

                                valList.(subv)
                            }
                        }
                    }

                    sub.add(keytxt)
                    sub.add(valList)
                    listArg.add(sub)
                }
            }
        }
        listArg.addAll(shouldBeFront)
    }

    private void handingArgs(TupleExpression arg, ArrayList inputArg){
        arg
    }

    private void handingArgs(TernaryExpression arg, ArrayList inputArg){
        arg
    }


    /*public def getMethodList(def args, String methodCall){


     ArrayList methodList = new ArrayList()
     ArrayList methodArgList = getArgList(args, methodCall)

     methodList.add(methodCall)
     methodList.addAll(methodArgList.reverse())

     return methodList
 }

 public def getArgList(def args, String methodCall){
     ArrayList listArg  = new ArrayList();
     this.methodCall = methodCall

     args.each { arg ->
         handingArgs(arg,listArg)
     }
     return listArg
 }*/

}
