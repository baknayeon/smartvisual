package node

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
class Method {

    boolean dynamic = false
    String methodName
    def parameter
    Stack level
    def code

    public Method(Stack level, def code){
        this.level = level
        this.code = code
    }

    public Method(String methodName, def parameter){
        this.methodName = methodName
        this.parameter = parameter

    }

    private void handingArgs(ConstantExpression arg){
        def text = arg.getText()
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
                    if (keytxt.equals("name")){
                        setMethodName(valtxt)
                    }

                }else if(valExpr instanceof GStringExpression){
                    def valtxt = ((ConstantExpression)((java.util.ArrayList)((GStringExpression)valExpr).strings).get(0)).value
                    if (keytxt.equals("name")){
                        setMethodName(valtxt)
                    }

                }else if(valExpr instanceof ListExpression){
                    def arrayList = (java.util.ArrayList) ((ListExpression) valExpr).expressions
                    setOption(arrayList)
                }
            }
        }
    }


    private void handingArgs(PropertyExpression arg){
        def text = ((PropertyExpression) arg).getText()
    }

    private void handingArgs(VariableExpression argvex){
        def text = argvex.getName()
    }

    private void handingArgs(GStringExpression arg){
        def text = arg.verbatimText.toString()
    }
    private void handingInputArgs(NamedArgumentListExpression arg){

        def arrayList = (NamedArgumentListExpression) arg
        setOption(arrayList)
    }


    private void handingArgs(ClosureExpression args){

    }


    private void handingInputArgs(TupleExpression arg, ArrayList inputArg){
        arg
    }

    private void handingInputArgs(TernaryExpression arg, ArrayList inputArg){
        arg
    }
}


