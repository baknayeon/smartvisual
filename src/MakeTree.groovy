import node.Method
import node.Href
import node.Input
import node.Label
import node.Page
import node.Section
import node.Subscribe

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MapExpression

import javax.swing.tree.DefaultMutableTreeNode

class MakeTree {

    private DefaultMutableTreeNode preferences

    private int preferLevel

    ArrayList preferList
    ArrayList subscribeList
    ArrayList dynamicPageList


    public DefaultMutableTreeNode getPage(){

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root")
        preferences = new DefaultMutableTreeNode("preferences")

        preferLevel = 0
        String preferencesText = preferList.get(preferLevel)
        if(preferencesText.equals("preferences")){
            ++preferLevel
            while(preferLevel< preferList.size()){

                def object = preferList.get(preferLevel)

                if(object instanceof Page){
                    Page page = object
                    if(page.getType().equals("page")) {
                        preferences.add(makePage())
                    }else if(page.getType().equals("dynamicPage")) {
                        def dynamicPage = makeDynamicPage(page.getName())
                        if(dynamicPage)
                            preferences.add(dynamicPage)
                        ++preferLevel
                    }
                }else if(object instanceof String){
                    String if_text = object
                    ++preferLevel
                    preferences.add(new DefaultMutableTreeNode(if_text))
                }else if(object instanceof Section){
                    preferences.add(makeSection())
                }
            }
        }

        DefaultMutableTreeNode location = new DefaultMutableTreeNode("Location")
        DefaultMutableTreeNode app = new DefaultMutableTreeNode("App")

        makeHandler(location, "location")
        makeHandler(app, "app")

        root.add(location)
        root.add(app)
        root.add(preferences)


        return root
    }

    private def makePage() {
        Page pageList = preferList.get(preferLevel)
        def page = new DefaultMutableTreeNode("Page "+pageList.name?.toString())

        ++preferLevel
        while( preferLevel < preferList.size()) {
            def nextList = preferList.get(preferLevel)

            if(nextList in Page)
                return page
            else if (nextList in String) {
                page.add(new DefaultMutableTreeNode(nextList))
                ++preferLevel
            } else {
                if (nextList in Section)
                    page.add(makeSection())
            }
        }

        return page
    }

    private def makeSection(){

        Section sectionList = preferList.get(preferLevel)

        def section = new DefaultMutableTreeNode("section "+sectionList.title?.toString())

        ++preferLevel
        while ( preferLevel < preferList.size() ){
           def nextList = preferList.get(preferLevel)
            if(nextList in Section || nextList in Page) //|| nextType.equals("dynamicPage"))
                return section
            else if(nextList in String){
                section.add(new DefaultMutableTreeNode(nextList))
                ++preferLevel
            }
            else{
                if(nextList in Input || nextList in Href || nextList in Label){
                    def leafNode
                    if(nextList in Input ){
                        Input input = nextList
                        leafNode = new DefaultMutableTreeNode("input " + input.getName())
                    }else if(nextList in Href ){
                        Href href = nextList
                        leafNode = new DefaultMutableTreeNode("href " + href.getPage()?.toString())
                    }else if(nextList in Label ){
                        Label label = nextList
                        leafNode = new DefaultMutableTreeNode("label " + label.getName())
                    }
                    makLeafNodeArgs(leafNode, nextList)

                    section.add(leafNode)
                    preferLevel++
                }
            }
        }

        return section
    }

    private def makLeafNodeArgs(DefaultMutableTreeNode node, def type){

        if(type in Input) {
            Input input = type
            makeInputArg(node, input)
        }else if(type in Href){
            Href href = type
            //node.add(new DefaultMutableTreeNode(href.getPage()?.toString()))

        }else if(type in Label){
            Label label = type
            //node.add(new DefaultMutableTreeNode(label.getName()))
        }
        //leaf_sub.add(node)

    }

    private void makeInputArg(def node, Input input){

        node.add(new DefaultMutableTreeNode(input.getCapability()))

        ArrayList option = input.getOption();
        option.each{ def entry ->
            if(entry in Input){
                def sbuInput = new DefaultMutableTreeNode("input")
                makeInputArg(sbuInput , entry)
                node.add(sbuInput)
            }
        }

        makeHandler(node, input.getName())
    }

    public void makeHandler(DefaultMutableTreeNode node, String name){

        int i =0
        if(!name.equals("")) {

            //여러 핸들러 등록가능
            subscribeList.each{ Subscribe sub ->
                if(!sub.getError()) {
                    String subInput = sub.getInput()
                    if (subInput.equals(name)) {
                          //sub.setMatched(true)
                        def handler = new DefaultMutableTreeNode(sub.getHandler())
                        handler.add(new DefaultMutableTreeNode(sub.getCapability()))
                        node.add(handler)
                    }
                }
            }
        }
    }

    DefaultMutableTreeNode makeDynamicPage(String dynamicPageName){

        for(Method dynamicMethod in dynamicPageList){

            String methodNamce = dynamicMethod.getMethodName()
            if(dynamicPageName.equals(methodNamce)){
                def nodes = makeDynamicNodes(dynamicMethod.getLevel(), dynamicMethod.getCode())
                DefaultMutableTreeNode dynamicPage = generate(nodes.pop())
                DefaultMutableTreeNode dynamicMethodNew = new DefaultMutableTreeNode("dynamicMethod " +methodNamce)
                dynamicMethodNew.add(dynamicPage)

                return dynamicMethodNew
            }
        }
    }

    private def makeDynamicNodes(Stack dynamicLevelStack, ArrayList dynamicMethodList) {
        Stack sub = new Stack<>()
        sub  = dynamicLevelStack.clone()
        Stack nodes = new Stack()
        int oldLevel

        while (!sub.empty()){

            int newLevel = sub.pop()
            int index = sub.size()

            if(!nodes.empty()){

                if( newLevel < oldLevel ) { //부모랑 자식 붙이기
                    def parent = new DefaultMutableTreeNode(new Node(dynamicMethodList.get(index), newLevel))

                    while(!nodes.empty()){
                        def child = nodes.pop()
                        int oldLevel_node = ((Node)((DefaultMutableTreeNode)child).getUserObject()).getLevel()

                        if( newLevel < oldLevel_node )
                            parent.add(child)
                        else{
                            nodes.push(child)
                            break
                        }
                    }

                    nodes.push(parent)
                }else{
                    def leafNode = new DefaultMutableTreeNode(new Node(dynamicMethodList.get(index), newLevel))
                    nodes.push(leafNode)

                }
            }else{
                def leafNode = new DefaultMutableTreeNode(new Node(dynamicMethodList.get(index), newLevel))

                nodes.push(leafNode)
            }

            oldLevel = newLevel
        }

        return nodes
    }

    private DefaultMutableTreeNode generate(def root) {

        Enumeration en = root.depthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Node userObject = node.getUserObject()

            setNode(userObject.getNode() , node)
        }

        return root
    }

    private DefaultMutableTreeNode generate(def root, Method caller) {

        Enumeration en = root.depthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Node userObject = node.getUserObject()

            if(userObject.getNode() in Input){
                Input input = new Input(caller.parameter)
                setNode(input , node)
            }else{
                setNode(userObject.getNode() , node)
            }
        }

        return root
    }

    private def setNode(Input input, DefaultMutableTreeNode node){

        node.setUserObject(new DefaultMutableTreeNode("input " + input.getName()))
        makLeafNodeArgs(node, input)

        return node
    }

    private def setNode(Href href, DefaultMutableTreeNode node){

        node.setUserObject(new DefaultMutableTreeNode("href " + href.getPage()?.toString()))
        makLeafNodeArgs(node, href)

        return node
    }

    private def setNode(Label label, DefaultMutableTreeNode node){


        node.setUserObject(new DefaultMutableTreeNode("label " + label.getName()))
        makLeafNodeArgs(node, label)

        return node
    }

    private def setNode(Section section, DefaultMutableTreeNode node){

        node.setUserObject(new DefaultMutableTreeNode("section "+section.getTitle()))

        return node
    }

    private def setNode(String name, DefaultMutableTreeNode node){

        def newNode = new DefaultMutableTreeNode(name.toString())
        node.setUserObject(newNode)

    }

    private def setNode(Page page, DefaultMutableTreeNode node){

        if(page.getType().equals("dynamicPage")){

            node.setUserObject(new DefaultMutableTreeNode("dynamicPage " +page.getName()))

        } else if(page.getType().equals("methodCall")) {

            for(Method dynamicMethod  : dynamicPageList) {
                String caller_Page = page.getName()
                String called_Page = dynamicMethod.getMethodName()

                if (caller_Page.equals(called_Page)) {
                    def nodes = makeDynamicNodes(dynamicMethod.getLevel(), dynamicMethod.getCode())
                    DefaultMutableTreeNode dynamicPage = generate(nodes.pop())
                    node.add(dynamicPage)
                }
            }
            node.setUserObject(new DefaultMutableTreeNode("dynamicMethod "+ page.getName()))

        }

        return node
    }

    private def setNode(Method caller, DefaultMutableTreeNode node){

        for(Method called : dynamicPageList){
            if(sameMethod(caller, called)){

                def nodes_CalledMethod = makeDynamicNodes(called.getLevel(), called.getCode())
                def dynamicNode = generate(nodes_CalledMethod.pop(), caller)

                node.add(dynamicNode)
                node.setUserObject(new DefaultMutableTreeNode(called.getMethodName()))
                break
            }
        }
        return node
    }

    private boolean sameMethod(Method caller, Method called){

        boolean result = false

        if(called.getMethodName().equals(caller.getMethodName())){
            ArrayList caller__P = (java.util.ArrayList)(caller.parameter)
            Parameter[] called__P = called.getParameter()

            if(caller__P.size() == called__P.size()){
                int i = 0

                while(i < caller__P.size()){
                    String caller_PType
                    String called_PType = called__P[i].type.name

                    if(caller__P.get(i) in MapExpression)
                        caller_PType = ((ClassNode)((MapExpression)caller__P.get(i)).type).name
                    else if(caller__P.get(i) in ConstantExpression)
                        caller_PType = ((ClassNode)((ConstantExpression)caller__P.get(i)).type).name

                    if(caller_PType.contains(called_PType))
                        result = true
                    else
                        result = false

                    i++
                }
            }
        }
        return result
    }


    class Node {
        private def node;
        private int nodeLevel;

        public Node(def list, int level){
            node = list;
            nodeLevel = level;
        }
        public def getNode(){
            return node;
        }
        public def getLevel(){
            return nodeLevel;
        }
    }

}

