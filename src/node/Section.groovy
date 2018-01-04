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
class Section {

    String type
    String title = ""
    def option

    public Section(){
    }

    public Section(def args){
        option = new ArrayList()
        this.type = "section"
        args.each { arg ->
            handingArgs(arg)
        }
    }

    void setOption(def option){
        this.option = option
    }

    String getType() {
        return type
    }

    void setType(String nodeType) {
        this.type = nodeType
    }

    String getTitle() {
        return title
    }

    void setTitle(String name) {
        this.title = name
    }

    String getCapability() {
        return capability
    }

    void setCapability(String capability) {
        this.capability = capability
    }

    private void handingArgs(ConstantExpression arg){
        def text = arg.getText()
        if(getTitle() == null)
            setTitle(text)
    }

    private void handingArgs(PropertyExpression arg){
        def text = ((PropertyExpression) arg).getText()
        if(getTitle() == null)
            setTitle(text)
    }

    private void handingArgs(VariableExpression argvex){
        def text = argvex.getName()
        if(getTitle() == null)
            setTitle(text)
    }

    private void handingArgs(GStringExpression arg){
        def text = arg.verbatimText.toString()
        if(getTitle() == null)
            setTitle(text)
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
                        setTitle(valtxt)
                    }

                }else if(valExpr instanceof GStringExpression){
                    def valtxt = ((ConstantExpression)((java.util.ArrayList)((GStringExpression)valExpr).strings).get(0)).value
                    if (keytxt.equals("title")){
                        setTitle(valtxt)
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


    }

    private void handingInputArgs(TupleExpression arg){
        arg
    }

    private void handingInputArgs(TernaryExpression arg){
        arg
    }

    def methodMissing(String name, def args) {
        return null
    }
}

