package preferenceNode

/**
 * Created by b_newyork on 2017-09-27.
 */

class Capability {

    String capability  = ""
    String attribute  = ""
    ArrayList attr_val = new ArrayList()
    ArrayList commands  = new ArrayList()

    public boolean checkAttrVal(String newVal){
        if(attr_val.size() == 0)
            return true
        for(String val : attr_val){
            if(newVal.equals(val))
                return true
        }
        return false
    }

}