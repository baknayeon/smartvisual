package preferenceNode

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression

/**
 * Created by b_newyork on 2017-09-11.
 */
class Page {

    String type
    String name
    String title
    def parameter

    public Page(String methodName, def parameter,String type){
        this.type = type
        this.name = methodName
        this.parameter = parameter
    }

    public Page(def args, String type){
        this.type = type
        args.each { arg ->
            handingArgs(arg)
        }
    }

    private void handingArgs(ConstantExpression arg){
        def text = arg.getText()
        if(getName() == null)
            setName(text)
        else
            setType(text)
    }

    private void handingArgs(MapExpression arg){

        arg?.mapEntryExpressions.each{ inner ->

            def keyExpr = inner.getKeyExpression()
            def valExpr = inner.getValueExpression()

            if (keyExpr instanceof ConstantExpression) {
                def keytxt = ((ConstantExpression) keyExpr).getText()

                if(valExpr instanceof ConstantExpression){
                    def valtxt = ((ConstantExpression) valExpr).getText()
                    if (keytxt.equals("name"))
                        setName(valtxt)
                    else if(keytxt.equals("title"))
                        setTitle(valtxt)

                }else if(valExpr instanceof GStringExpression){
                    def valtxt = ((ConstantExpression)((java.util.ArrayList)((GStringExpression)valExpr).strings).get(0)).value
                    if (keytxt.equals("name"))
                        setName(valtxt)
                    else if(keytxt.equals("title"))
                        setTitle(valtxt)

                }else if(valExpr instanceof ListExpression){

                }
            }
        }
    }

    private void handingArgs(ClosureExpression args){}
    def methodMissing(String name, def args) {
        return null
    }
}


