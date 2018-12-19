package support;

/**
 * Created by b_newyork on 2018-08-06.
 */

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import java.awt.*;
import java.util.*;


public class EventFlow {

    private HashMap event2actionMap;
    private mxGraph graph;
    private int Y = 55;
    private int X = 25  ;
    private int width = 150;
    private int height = 30;

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
        Object eventNode = graph.insertVertex(defaultParent, event, event,  20, 10, width, height,"shape=ellipse;strokeColor=black;fillColor=#4ac0bd;fontColor=black;fontSize=12");
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

                    int length = method1.length()*7;
                    mxCell childnode;
                    if(length > width)
                        childnode = (mxCell)graph.insertVertex(defaultParent, method1, method1,  x +j*X, y + Y, length, height,"strokeColor=black;fillColor=white;fontColor=black;fontSize=12");
                    else
                        childnode = (mxCell)graph.insertVertex(defaultParent, method1, method1,  x +j*X, y + Y, width, height,"strokeColor=black;fillColor=white;fontColor=black;fontSize=12");

                    graph.insertEdge(defaultParent, null, " ", parent, childnode,"strokeColor=black;fontSize=12");
                    parent = childnode;
                }
            }
            double x = parent.getGeometry().getX();
            double y = parent.getGeometry().getY();
            int j = 0;
            while(exist(x + j*X, y + Y))
                j++;


            String action = (String) methodsList.get(methodsList.size()-1);

            int length = action.length()*7;
            mxCell actionNode;
            if(length > width)
                 actionNode = (mxCell)graph.insertVertex(defaultParent, action, action,  x +j*X, y + Y, length, height,"shape=ellipse;strokeColor=black;fillColor=#e5cc0d;fontColor=black;fontSize=12");
            else
                actionNode = (mxCell)graph.insertVertex(defaultParent, action, action,  x +j*X, y + Y, width, height,"shape=ellipse;strokeColor=black;fillColor=#e5cc0d;fontColor=black;fontSize=12");

            graph.insertEdge(defaultParent, null, " ", parent, actionNode,"strokeColor=black;dashed=1;fontSize=12");
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
            double cell_x = cell.getGeometry().getX();
            double cell_width = cell.getGeometry().getWidth();
            double cell_y =cell.getGeometry().getY() ;
            if(cell_y == y ) { // alread exit
                if( x <= (cell_x + cell_width ) && cell_x <= x  )
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