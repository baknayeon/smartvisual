package support;

/**
 * Created by b_newyork on 2018-08-06.
 */

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.awt.*;
import java.util.*;


public class EventFlow {

    private HashMap event2actionMap;
    private mxGraph graph;
    private int Y = 35;
    private int X = 140;
    private int width = 130;
    private int height = 20;

    public EventFlow(HashMap event2actionMap) {
        this.event2actionMap = event2actionMap;
    }

    public Iterator getIterator(){
        return event2actionMap.keySet().iterator();
    }

    public mxGraphComponent getGraph(String event){
        graph = new mxGraph();
        Object defaultParent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        ArrayList methodFlows = (ArrayList)event2actionMap.get(event);
        Object eventNode = graph.insertVertex(defaultParent, event, event,  20, 10, width, height,"strokeColor=black;fillColor=#4ac0bd;fontColor=black");
        mxCell parent = (mxCell)eventNode;
        for(Object obj3 : methodFlows) {
            ArrayList methodsList = (ArrayList)obj3;
            for(int i = 1; i < methodsList.size()-1; i++) {
                String method1 = (String) methodsList.get(i);

                if(exist(method1)){
                    parent = getCell(method1);
                }else{
                    double x = parent.getGeometry().getX();
                    double y = parent.getGeometry().getY();
                    int j = 0;

                    while(exist(x + j*X, y + Y))
                        j++;

                    mxCell childnode = (mxCell)graph.insertVertex(defaultParent, method1, method1,  x +j*X, y + Y, width, height,"strokeColor=black;fillColor=white;fontColor=black");
                    if(parent.getId().equals(event))
                        graph.insertEdge(defaultParent, null, " ", parent, childnode,"strokeColor=black;dashed=1");
                    else
                        graph.insertEdge(defaultParent, null, " ", parent, childnode,"strokeColor=black;");
                    parent = childnode;
                }
            }
            double x = parent.getGeometry().getX();
            double y = parent.getGeometry().getY();
            int j = 0;
            while(exist(x + j*X, y + Y))
                j++;


            String action = (String) methodsList.get(methodsList.size()-1);
            mxCell actionNode = (mxCell)graph.insertVertex(defaultParent, action, action,  x +j*X, y + Y, width, height,"strokeColor=black;fillColor=#e5cc0d;fontColor=black");
            graph.insertEdge(defaultParent, null, " ", parent, actionNode,"strokeColor=black;");
        }


        graph.getModel().endUpdate();

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setEnabled(false);
        graphComponent.setPageBorderColor(Color.white);
        return graphComponent;
    }

    protected boolean exist(String method1){
        Object[] cells = graph.getChildVertices(graph.getDefaultParent());
        for (Object c : cells)
        {
            mxCell cell = (mxCell) c;
            if(cell.getId().equals(method1)) { // alread exit
                return true;
            }
        }
        return false;
    }
    protected boolean exist(double x, double y){
        Object[] cells = graph.getChildVertices(graph.getDefaultParent());
        for (Object c : cells)
        {
            mxCell cell = (mxCell) c;
            if(cell.getGeometry().getX() == x && cell.getGeometry().getY() == y ) { // alread exit
                return true;
            }
        }
        return false;
    }
    protected mxCell getCell(String method1){
        Object[] cells = graph.getChildVertices(graph.getDefaultParent());
        for (Object c : cells)
        {
            mxCell cell = (mxCell) c;
            if(cell.getId().equals(method1)) { // alread exit
                return cell;
            }
        }
        return null;
    }

}