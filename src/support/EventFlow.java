package support;

/**
 * Created by b_newyork on 2018-08-06.
 */

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import node.DeviceAction;

import java.awt.*;
import java.util.*;


public class EventFlow {

    mxGraphComponent graphComponent;
    HashMap action_methodssMap;

    public EventFlow(HashMap ActionsCommandMap) {
        action_methodssMap = ActionsCommandMap;



        graphComponent = new mxGraphComponent(getGraph());
        graphComponent.setEnabled(false);
        graphComponent.setPageBorderColor(Color.white);

        //graphComponent.setPageBackgroundColor(Color.white);
        //graphComponent.setPageBorderColor(Color.white);
    }

    public mxGraphComponent getEventFlow() {
        return graphComponent;
    }

    private mxGraph getGraph(){
        mxGraph graph = new mxGraph();
        graph.getStylesheet();
        Object defaultParent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        int x = 0;
        Iterator<String> keys = action_methodssMap.keySet().iterator();
        while( keys.hasNext() ){
            String device = keys.next();
            DeviceAction methodFlows =  (DeviceAction)action_methodssMap.get(device);
            for(Object obj2 :  methodFlows.getCommands()){
                String commads = (String)obj2;
                ArrayList methodFlow = methodFlows.getMethodFlow(commads);
                for(Object obj3 : methodFlow){
                    ArrayList methodsList = (ArrayList)obj3;
                    Collections.reverse(methodsList);
                    int j=0;
                    for(int i = 0; i < methodsList.size()-1; i++){
                        j = i+1;
                        String method1 = (String)methodsList.get(i);
                        String method2 = (String)methodsList.get(j);
                        Object node1;
                        if(i == 0)
                            node1 = graph.insertVertex(defaultParent, null, method1,  20+ x*150, 20+ i*30, 130, 20,"strokeColor=black;fillColor=#4ac0bd;fontColor=black");
                        else
                            node1 = graph.insertVertex(defaultParent, null, method1,  20+ x*150, 20+ i*30, 130, 20,"strokeColor=black;fillColor=white;fontColor=black");
                        Object node2 = graph.insertVertex(defaultParent, null, method2, 20+ x*150, 20+ i*30+30, 130, 20,"strokeColor=black;fillColor=white;fontColor=black");
                        graph.insertEdge(defaultParent, null, " ", node1, node2,"strokeColor=black;");
                    }
                    String method1 = (String)methodsList.get(j);
                    Object node1 = graph.insertVertex(defaultParent, null, method1, 20+ x*150, 20+j*30, 130, 20,"strokeColor=black;fillColor=white;fontColor=black");
                    Object node2 = graph.insertVertex(defaultParent, null, device+"."+commads+"()", 20+ x*150, 20+j*30+30, 130, 20, "strokeColor=black;fillColor=#e5cc0d;fontColor=black");
                    graph.insertEdge(defaultParent, null, " ", node1, node2,"strokeColor=black;");
                    x++;
                }
            }
        }

        graph.getModel().endUpdate();

       return graph;
    }

}