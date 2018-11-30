package support

import node.SmartApp
import preferenceNode.Capability
import preferenceNode.Input

/**
 * Created by b_newyork on 2017-09-01.
 */
final class CapHelper {

    public static Map allCommands = new HashMap()
    static HashMap cap = new HashMap()


    public CapHelper(){
        loadCapRef()
    }

    static public boolean rightCommand(String command){
        ArrayList<String> commandsList = cap.values()
        for(String commands : commandsList){
            if (commands.contains(";"+command+";"))
                return true
        }
        return false
    }


    static public boolean rightCommand(String capability, String command){

        if(command.equals("collect")){
            return false
        }else {
            if (cap.containsKey(capability)) {
                String commands = cap.get(capability)
                if (commands.contains(";"+command+";"))
                    return true
                else
                    return false
            } else
                return false
        }
    }
    static Capability getCap(String capName){
        Capability cap = allCommands[capName]
        return cap
    }

    static boolean rightCommand2(String capName, String com){
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

        String capability = "capability."
        //accelerationSensor
        //actuator
        cap.put(capability +"airConditionerMode", ";setAirConditionerMode;")
        //airQualitySensor
        cap.put(capability +"alarm", ";both;off;siren;strobe;")
        cap.put(capability +"audioMute", ";setMute;mute;unmute;")
        cap.put(capability +"audioNotification", ";playTrack;playTrackAndResume;playTrackAndRestore;")
        //audioTrackData
        cap.put(capability +"audioVolume", ";setVolume;volumeUp;volumeDown;")
        //battery
        //beacon
        //bridge
        cap.put(capability +"bulb", ";off;on;")
        //button
        //carbonDioxideMeasurement
        //carbonMonoxideDetector
        cap.put(capability +"colorControl", ";setColor;setHue;setSaturation;")
        cap.put(capability +"colorTemperature", ";setColorTemperature;")
        cap.put(capability +"color", ";setColorValue;")
        cap.put(capability +"colorMode", ";colorMode;")
        cap.put(capability +"configuration", ";configure;")
        cap.put(capability +"consumable", ";setConsumableStatus;")
        //contactSensor
        cap.put(capability +"demandResponseLoadControl", ";requestDrlcAction;overrideDrlcAction;")
        cap.put(capability +"dishwasherMode", ";setDishwasherMode;")
        cap.put(capability +"dishwasherOperatingState", ";setMachineState;")
        cap.put(capability +"doorControl", ";close;open;")
        cap.put(capability +"dryerMode", ";setDryerMode;")
        cap.put(capability +"dryerOperatingState", ";setMachineState;")
        //dustSensor
        //energyMeter
        //estimatedTimeOfArrival
        cap.put(capability +"execute", ";execute;")

        cap.put(capability +"fanSpeed", ";setFanSpeed;")
        //filterStatus
        cap.put(capability +"garageDoorControl", ";close;open;")
        //geolocation
        //holdableButton
        //illuminanceMeasurement
        cap.put(capability +"imageCapture", ";take;")
        cap.put(capability +"indicator", ";indicatorNever;indicatorWhenOff;indicatorWhenOn;")
        cap.put(capability +"infraredLevel", ";setInfraredLevel;")
        cap.put(capability +"light", ";off;on;")
        cap.put(capability +"lockOnly", ";lock;")
        cap.put(capability +"lock", ";lock;unlock;")
        cap.put(capability +"mediaController", ";startActivity;")
        cap.put(capability +"mediaInputSource", ";setInputSource;")
        cap.put(capability +"mediaPlaybackRepeat", ";setPlaybackRepeatMode;")
        cap.put(capability +"mediaPlaybackShuffle", ";setPlaybackShuffle;")
        cap.put(capability +"mediaPlayback", ";setPlaybackStatus;play;pause;stop;")
        cap.put(capability +"mediaPresets", ";setInputSource;playPreset;")
        cap.put(capability +"mediaTrackControl", ";nextTrack;previousTrack;")
        cap.put(capability +"momentary", ";push;")
        //motionSensor
        cap.put(capability +"musicPlayer", ";mute;nextTrack;pause;play;playTrack;previousTrack;restoreTrack;resumeTrack;stop;unmute;setLevel;setTrack;")
        cap.put(capability +"notification", ";deviceNotification;")
        //odorSensor
        cap.put(capability +"outlet", ";off;on;")
        cap.put(capability +"ovenMode", ";setOvenMode;")
        cap.put(capability +"ovenOperatingState", ";setMachineState;stop;")
        cap.put(capability +"ovenSetpoint", ";setOvenSetpoint;")
        //pHMeasurement
        cap.put(capability +"polling", ";poll;")
        //powerConsumptionReport
        //powerMeter
        //powerSource
        //presenceSensor
        cap.put(capability +"rapidCooling", ";setRapidCooling;")
        cap.put(capability +"refresh", ";refresh;")
        cap.put(capability +"refrigerationSetpoint", ";setRefrigerationSetpoint;")
        //relativeHumidityMeasurement
        cap.put(capability +"relaySwitch", ";off;on;")
        cap.put(capability +"robotCleanerCleaningMode", ";setRobotCleanerCleaningMode;")
        cap.put(capability +"robotCleanerMovement", ";setRobotCleanerMovement;")
        cap.put(capability +"robotCleanerTurboMode", ";setRobotCleanerTurboMode;")
        //sensor
        //shockSensor
        //signalStrength
        ///sleepSensor
        //smokeDetector
        //soundPressureLevel
        //soundSensor
        //speechRecognition
        cap.put(capability +"speechSynthesis", ";speak;")
        //stepSensor
        cap.put(capability +"switchLevel", ";setLevel;")
        cap.put(capability +"switch", ";off;on;")

        //tamperAlert
        //temperatureMeasurement

        cap.put(capability +"thermostatCoolingSetpoint", ";setCoolingSetpoint;")
        cap.put(capability +"thermostatFanMode", ";fanAuto;fanCirculate;fanOn;setThermostatFanMode;")
        cap.put(capability +"thermostatHeatingSetpoint", ";setHeatingSetpoint;")
        cap.put(capability +"thermostatMode", ";auto;cool;emergencyHeat;heat;off;setThermostatMode;")
        //thermostatOperatingState
        //thermostatSetpoint
        cap.put(capability +"thermostat", ";auto;cool;emergencyHeat;fanAuto;fanCirculate;fanOn;heat;off;setCoolingSetpoint;setHeatingSetpoint;setSchedule;setThermostatFanMode;setThermostatMode")
       //threeAxis
        cap.put(capability +"timedSession", ";cancel;pause;setCompletionTime;start;stop;")
        cap.put(capability +"tone", ";beep;")
        //touchSensor
        cap.put(capability +"tvChannel", ";setTvChannel;channelUp;channelDown;")
        //ultravioletIndex
        cap.put(capability +"valve", ";close;open;")
        cap.put(capability +"videoClips", ";captureClip;")
        cap.put(capability +"videoStream", ";startStream;stopStream;")
        //voltageMeasurement
        cap.put(capability +"washerMode", ";setWasherMode;")
        cap.put(capability +"washerOperatingState", ";setMachineState;")
        //waterSensor
        cap.put(capability +"windowShade", ";presetPosition;open;close;")
    }

}
