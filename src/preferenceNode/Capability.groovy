package preferenceNode

/**
 * Created by b_newyork on 2017-09-27.
 */

class Capability {

    String capability
    String attribute
    ArrayList cap_val



    public Capability() {
        cap_val = new ArrayList()
        attribute = ""
    }

    public boolean checkVal(String newVal){
        if(cap_val.size() == 0)
            return true
        for(String val : cap_val){
            if(newVal.equals(val))
                return true
        }
        return false
    }


}