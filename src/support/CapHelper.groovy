package support

import preferenceNode.Capability
import preferenceNode.Input
import preferenceNode.Subscribe

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
    static def loadCapRef() {

        def capsAsPerSamsungFile = "./" + "Capabilities.csv"
        def file = new File(capsAsPerSamsungFile)

        file.splitEachLine(",") { fields ->

            Capability cap =  new Capability()

            String Cap = fields[1]?.toString()
            cap.setCapability(Cap)

            String dev = fields[2]?.toString()
            cap.setDevice(dev)

            fields[3]?.split(" ")?.each {
                cap.cap_val.add(it.toString())
            }

            allCommands[Cap] =  cap
        }
    }

}
