package preferenceNode

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Created by b_newyork on 2017-09-11.
 */
class Label {

    String name
    String capability
    ArrayList option

    public Label(def args) {
        option = new ArrayList()

        args.each { arg ->
            handingArgs(arg)
        }
    }

    private void handingArgs(ConstantExpression arg) {
        def text = arg.getText()
        if (getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(PropertyExpression arg) {
        def text = ((PropertyExpression) arg).getText()
        if (getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(VariableExpression argvex) {
        def text = argvex.getName()
        if (getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(GStringExpression arg) {
        def text = arg.verbatimText.toString()
        if (getName() == null)
            setName(text)
        else setCapability(text)
    }

    private void handingArgs(MapExpression arg) {

        arg?.mapEntryExpressions.each { inner ->

            def keyExpr = inner.getKeyExpression()
            def valExpr = inner.getValueExpression()

            if (keyExpr instanceof ConstantExpression) {
                def keytxt = ((ConstantExpression) keyExpr).getText()


                def sub = new ArrayList();
                if (valExpr instanceof ConstantExpression) {
                    def valtxt = ((ConstantExpression) valExpr).getText()
                    if (keytxt.equals("title")) {
                        setName(valtxt)
                    } else if (keytxt.equals("type")) {
                        setCapability(valtxt)
                    } else {

                    }

                } else if (valExpr instanceof GStringExpression) {
                    def valtxt = ((ConstantExpression) ((java.util.ArrayList) ((GStringExpression) valExpr).strings).get(0)).value

                    if (keytxt.equals("title")) {
                        setName(valtxt)
                    } else if (keytxt.equals("type")) {
                        setCapability(valtxt)
                    } else {

                    }
                } else if (valExpr instanceof ListExpression) {
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

        ((java.util.ArrayList)((BlockStatement)args.code).statements).eachWithIndex{ def entry, int j ->
            def subArgs =  ((MethodCallExpression)((ExpressionStatement)entry).expression).arguments.expressions
            Input i = new Input(subArgs)
            option.add(i)//?
        }
    }

}

