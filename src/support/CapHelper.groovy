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


    static public int rightCommand(String capability, String command){
        String deviceCommand = ";currentState;currentValue;events;eventsBetween;eventsSince;" +
                "getCapabilities;getDeviceNetworkId;getDisplayName;getHub;getId;getLabel;getLastActivity;getManufacturerName;getModelName;getStatus;getName;getSupportedAttributes;getSupportedCommands;getTypeName;" +
                "hasAttribute;hasCapability;hasCommand;latestState;latestValue;statesBetween;statesSince;" //current
        String methods = ";collect;findAll;find;count;size;"

        if(capability.startsWith("capability.")) {
            if (cap.containsKey(capability)) {
                String commands = cap.get(capability)
                if (commands.contains(";" + command + ";"))
                    return 1
                else if (deviceCommand.contains(";" + command + ";"))
                    return -3 //method
                else if (methods.contains(";" + command + ";"))
                    return -3 //method
                else if (command.endsWith("State"))
                    return -3 //method
                else if (command.endsWith("current"))
                    return -3 //method
                else
                    return 0 //unsupported command
            } else
                return -1 //unsupported cap

        }else
            return -2 //?
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

        cap.put(capability +"accelerationSensor", "")
        cap.put(capability +"actuator", "")
        cap.put(capability +"airConditionerMode", ";setAirConditionerMode;")
        cap.put(capability +"airQualitySensor", "")
        cap.put(capability +"alarm", ";both;off;siren;strobe;")
        cap.put(capability +"audioMute", ";setMute;mute;unmute;")
        cap.put(capability +"audioNotification", ";playTrack;playTrackAndResume;playTrackAndRestore;")
        cap.put(capability +"audioTrackData", "")
        cap.put(capability +"audioVolume", ";setVolume;volumeUp;volumeDown;")
        cap.put(capability +"battery", "")
        cap.put(capability +"beacon", "")
        cap.put(capability +"bridge", "")

        cap.put(capability +"bulb", ";off;on;")
        cap.put(capability +"button", "")
        cap.put(capability +"carbonDioxideMeasurement", "")
        cap.put(capability +"carbonMonoxideDetector", "")

        cap.put(capability +"colorControl", ";setColor;setHue;setSaturation;")
        cap.put(capability +"colorTemperature", ";setColorTemperature;")
        cap.put(capability +"color", ";setColorValue;")
        cap.put(capability +"colorMode", ";colorMode;")
        cap.put(capability +"configuration", ";configure;")
        cap.put(capability +"consumable", ";setConsumableStatus;")
        cap.put(capability +"contactSensor", "")

        cap.put(capability +"demandResponseLoadControl", ";requestDrlcAction;overrideDrlcAction;")
        cap.put(capability +"dishwasherMode", ";setDishwasherMode;")
        cap.put(capability +"dishwasherOperatingState", ";setMachineState;")
        cap.put(capability +"doorControl", ";close;open;")
        cap.put(capability +"dryerMode", ";setDryerMode;")
        cap.put(capability +"dryerOperatingState", ";setMachineState;")
        cap.put(capability +"dustSensor", "")
        cap.put(capability +"energyMeter", "")
        cap.put(capability +"estimatedTimeOfArrival", "")

        cap.put(capability +"execute", ";execute;")

        cap.put(capability +"fanSpeed", ";setFanSpeed;")
        cap.put(capability +"filterStatus", "")

        cap.put(capability +"garageDoorControl", ";close;open;")
        cap.put(capability +"geolocation", "")
        cap.put(capability +"holdableButton", "")
        cap.put(capability +"illuminanceMeasurement", "")

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
        cap.put(capability +"motionSensor", "")

        cap.put(capability +"musicPlayer", ";mute;nextTrack;pause;play;playTrack;previousTrack;restoreTrack;resumeTrack;stop;unmute;setLevel;setTrack;")
        cap.put(capability +"notification", ";deviceNotification;")
        cap.put(capability +"odorSensor", "")

        cap.put(capability +"outlet", ";off;on;")
        cap.put(capability +"ovenMode", ";setOvenMode;")
        cap.put(capability +"ovenOperatingState", ";setMachineState;stop;")
        cap.put(capability +"ovenSetpoint", ";setOvenSetpoint;")
        cap.put(capability +"pHMeasurement", "")

        cap.put(capability +"polling", ";poll;")
        cap.put(capability +"powerConsumptionReport", "")
        cap.put(capability +"powerMeter", "")
        cap.put(capability +"powerSource", "")
        //
        cap.put(capability +"presenceSensor", "")
        //
        //
        //
        cap.put(capability +"rapidCooling", ";setRapidCooling;")
        cap.put(capability +"refresh", ";refresh;")
        cap.put(capability +"refrigerationSetpoint", ";setRefrigerationSetpoint;")
        cap.put(capability +"relativeHumidityMeasurement", "")
        //
        cap.put(capability +"relaySwitch", ";off;on;")
        cap.put(capability +"robotCleanerCleaningMode", ";setRobotCleanerCleaningMode;")
        cap.put(capability +"robotCleanerMovement", ";setRobotCleanerMovement;")
        cap.put(capability +"robotCleanerTurboMode", ";setRobotCleanerTurboMode;")
        cap.put(capability +"sensor", "")
        cap.put(capability +"shockSensor", "")
        cap.put(capability +"signalStrength", "")
        cap.put(capability +"sleepSensor", "")
        cap.put(capability +"smokeDetector", "")
        cap.put(capability +"soundPressureLevel", "")
        cap.put(capability +"soundSensor", "")
        cap.put(capability +"speechRecognition", "")
        //
        //
        //
        ///
        //
        //
        //
        //
        cap.put(capability +"speechSynthesis", ";speak;")
        cap.put(capability +"stepSensor", "")
        //
        cap.put(capability +"switchLevel", ";setLevel;")
        cap.put(capability +"switch", ";off;on;")
        cap.put(capability +"tamperAlert", "")
        cap.put(capability +"temperatureMeasurement", "")

        //
        //

        cap.put(capability +"thermostatCoolingSetpoint", ";setCoolingSetpoint;")
        cap.put(capability +"thermostatFanMode", ";fanAuto;fanCirculate;fanOn;setThermostatFanMode;")
        cap.put(capability +"thermostatHeatingSetpoint", ";setHeatingSetpoint;")
        cap.put(capability +"thermostatMode", ";auto;cool;emergencyHeat;heat;off;setThermostatMode;")
        cap.put(capability +"thermostatOperatingState", "")
        cap.put(capability +"thermostatSetpoint", "")
        //
        //
        cap.put(capability +"thermostat", ";auto;cool;emergencyHeat;fanAuto;fanCirculate;fanOn;heat;off;setCoolingSetpoint;setHeatingSetpoint;setSchedule;setThermostatFanMode;setThermostatMode")
        cap.put(capability +"threeAxis", "")
       //
        cap.put(capability +"timedSession", ";cancel;pause;setCompletionTime;start;stop;")
        cap.put(capability +"tone", ";beep;")
        cap.put(capability +"touchSensor", "")
        //
        cap.put(capability +"tvChannel", ";setTvChannel;channelUp;channelDown;")
        cap.put(capability +"ultravioletIndex", "")
        //
        cap.put(capability +"valve", ";close;open;")
        cap.put(capability +"videoClips", ";captureClip;")
        cap.put(capability +"videoStream", ";startStream;stopStream;")
        cap.put(capability +"voltageMeasurement", "")
        //
        cap.put(capability +"washerMode", ";setWasherMode;")
        cap.put(capability +"washerOperatingState", ";setMachineState;")
        cap.put(capability +"waterSensor", "")
        //
        cap.put(capability +"windowShade", ";presetPosition;open;close;")
    }

}
