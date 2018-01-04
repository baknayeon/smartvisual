package node

/**
 * Created by b_newyork on 2017-09-27.
 */

class Capability {

    String capability
    String device
    ArrayList cap_val

    public Capability() {
        cap_val = new ArrayList()
        device = ""
    }

    public boolean checkVal(String newVal){
        for(String val : cap_val){
            if(newVal.equals(val))
                return true
        }
        return false
    }
}