package node

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
 * Created by b_newyork on 2017-09-11.
 */
class Label {

    String type
    String name
    String capability
    ArrayList option

    public Label(){
    }

    public Label(def args){
        option = new ArrayList()

        this.type = "label"
        args.each { arg ->
            handingArgs(arg)
        }
    }


    void setOption(def option){
        this.option.add(option)
    }

    String getType() {
        return type
    }

    void setType(String nodeType) {
        this.type = nodeType
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getCapability() {
        return capability
    }

    void setCapability(String capability) {
        this.capability = capability
    }

    private void handingArgs(ConstantExpression arg){
        def text = arg.getText()
        if(getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(PropertyExpression arg){
        def text = ((PropertyExpression) arg).getText()
        if(getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(VariableExpression argvex){
        def text = argvex.getName()
        if(getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(GStringExpression arg){
        def text = arg.verbatimText.toString()
        if(getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(MapExpression arg){

        arg?.mapEntryExpressions.each{ inner ->

            def keyExpr = inner.getKeyExpression()
            def valExpr = inner.getValueExpression()

            if (keyExpr instanceof ConstantExpression) {
                def keytxt = ((ConstantExpression) keyExpr).getText()


                def sub = new ArrayList();
                if(valExpr instanceof ConstantExpression){
                    def valtxt = ((ConstantExpression) valExpr).getText()
                    if (keytxt.equals("title")){
                        setName(valtxt)
                    }else if(keytxt.equals("type")) {
                        setCapability(valtxt)
                    } else {

                    }

                }else if(valExpr instanceof GStringExpression){
                    def valtxt = ((ConstantExpression)((java.util.ArrayList)((GStringExpression)valExpr).strings).get(0)).value

                    if (keytxt.equals("title")){
                        setName(valtxt)
                    }else if(keytxt.equals("type")) {
                        setCapability(valtxt)
                    } else {

                    }
                }else if(valExpr instanceof ListExpression){
                    def arrayList = (java.util.ArrayList) ((ListExpression) valExpr).expressions
                    setOption(arrayList)
                }
            }
        }
    }

    private void handingInputArgs(NamedArgumentListExpression arg){

        def arrayList = (NamedArgumentListExpression) arg
        setOption(arrayList)
    }


    private void handingArgs(ClosureExpression args){

        Input i = new Input()
        ((java.util.ArrayList)((BlockStatement)args.code).statements).eachWithIndex{ def entry, int j ->
            handingClosureInputArgs(entry,  i)
        }
        setOption(i)
    }

    private void handingClosureInputArgs(ExpressionStatement args, Input input){

        ((java.util.ArrayList)(((MethodCallExpression)args.expression).arguments).expressions).eachWithIndex{ def entry, int i ->
            handingArgs(entry , input)
        }

    }

    private void handingInputArgs(TupleExpression arg, ArrayList inputArg){
        arg
    }

    private void handingInputArgs(TernaryExpression arg, ArrayList inputArg){
        arg
    }
}

