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
	File Afile = new File("evaluate.txt")
	File fileS = new File("totalevaluate.txt")

	public void appendAfile(String s)
	{
		Afile.append(System.getProperty("line.separator") + s)

	}

	public void appendfileS(String s)
	{
		fileS.append(System.getProperty("line.separator") + s)

	}
}
