/*
 * SmartThingsAnalysisTools Copyright 2016 Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 */

package support

class Logger 
{
	File file = new File("evaluate.txt")

	File simpleApp = new File("evaluate1SimpleApp.txt")
	File simpleApp2 = new File("evaluate12SimpleApp.txt")
	File frequentEvent = new File("evaluate2frequentEvent.txt")
	File frequentAction = new File("evaluate3frequentAction.txt")
	File frequentIntersection = new File("evaluate3frequentIntersection.txt")
	File notfrequentIntersection = new File("evaluate3notfrequentIntersection.txt")
	File event_action_deivce = new File("evaluate4eventactiondeivce.txt")
	File sms = new File("evaluate5sms.txt")


	String string = ""
	public Logger(def filename)
	{
		file = new File(filename)
	}

	public Logger()
	{

	}


	public void append(String s)
	{
		string = string +"\n"+ s

		//file.append(System.getProperty("line.separator") + s)
	}

	public void push(boolean[] resultM){
		if(resultM[0] ) {
			simpleApp.append(System.getProperty("line.separator") +string)
		}
		if(resultM[0] && !resultM[1] && !resultM[2] && !resultM[3] && !resultM[4]) {
			simpleApp2.append(System.getProperty("line.separator") +string)
		}
		if(resultM[1] && !resultM[2]) {
			frequentEvent.append(System.getProperty("line.separator") + string)
		}
		if(!resultM[1] && resultM[2]) {
			frequentAction.append(System.getProperty("line.separator") + string)
		}

		if(resultM[1] && resultM[2]) {
			frequentIntersection.append(System.getProperty("line.separator") + string)
		}

		if(!resultM[1] && !resultM[2]){
			notfrequentIntersection.append(System.getProperty("line.separator") + string)
		}

		if(resultM[3]){
			event_action_deivce.append(System.getProperty("line.separator") + string)

		}
		if(resultM[4]) {
			sms.append(System.getProperty("line.separator") + string)

		}
		file.append(System.getProperty("line.separator") + string)
	}
}
