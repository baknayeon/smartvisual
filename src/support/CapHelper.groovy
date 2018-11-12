package support

import preferenceNode.Capability

/**
 * Created by b_newyork on 2017-09-01.
 */
final class CapHelper {

    public static Map allCommands = new HashMap()

    public CapHelper(){
        loadCapRef()
    }

    static Capability getCap(String capName){
        Capability cap = allCommands[capName]
        return cap
    }

    static boolean rightCommand(String capName, String com){
        Capability cap = allCommands[capName]
        ArrayList commands = cap.getCommands()
        for(String command : commands){
            if(command.equals(com))
                return true
        }
        return false
    }

    static def loadCapRef() {

        def capsAsPerSamsungFile = "./" + "Capabilities.csv"
        def file = new File(capsAsPerSamsungFile)

        file.splitEachLine(",") { fields ->

            Capability cap =  new Capability()

            String Cap = fields[1]?.toString()
            cap.setCapability(Cap)

            String dev = fields[2]?.toString()
            cap.setAttribute(dev)

            fields[3]?.split(" ")?.each {
                cap.attr_val.add(it.toString())
            }

            fields[4]?.split(" ")?.each {
                cap.commands.add(it.toString())
            }

            allCommands[Cap] =  cap
        }
    }

}
