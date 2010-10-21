#!/usr/bin/env groovy

// helper scripts version 0.1.0

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.Properties
import jva.io.*

public class bdaProjectStartHelper
{
	// declare global variables, what can I say I am a perl scripter not a programmer
	static excludeTargetPatternList = []
	static excludePropertyPatternList = []
	static projectSearchString1 = 'bda-blueprints'
	static projectSearchString2 = 'blueprints-webapp'
	static projectReplaceString = ''
	static databaseTypeList = []
	static databasePreferred = ''
	static props = new Properties()
	static fileContentsBuffer = ""
	static projectBuildDir =""
	static templateDir = ""

	static targetExcList = []
	static targetIncList = []
	static targetBuffer =""

	public static void main (String[] args)
	{
		readProperties()
		println "Property list ${props.toString()}"
		buildFilterLists()
		processXmlFile('build.xml')
		processXmlFile('install.xml')
		filterPropertiesFile('project.properties')
		filterPropertiesFile('install.properties')
		filterPropertiesFile('upgrade.properties')
		filterPropertiesFile('properties.template')
	}

	private static void readProperties ()
	{
		// Reads props file
		def propertiesFile = new File('./helper.properties')
		def fileStream = new FileInputStream(propertiesFile)
		
		props.load(fileStream)
	}
		
	private static void buildFilterLists()
	{
		// sets local/global varibales based on the properties
		def useLdap=props.get('use.ldap')
		def useJboss=props.get('use.jboss')
		def useTomcat=props.get('use.tomcat')
		def useGrid=props.get('use.grid')
		def useGuiInstaller=props.get('use.gui-installer')
		def useMaven=props.get('use.maven')
		def projectRootDir=props.get('project.root.dir')

		projectReplaceString=props.get('project.prefix')
		databaseTypeList=props.get('database.type.list').split(',')
		databasePreferred=props.get('database.preferred')
		templateDir=props.get('bda.template.dir') + "/build"
		projectBuildDir= projectRootDir + "/software/build"

		// Clean up test targets
		excludeTargetPatternList << "^temp"

		// Conditionally builds exclude patter lists based on settings from props files
		if (useJboss != "true") 
		{
			excludeTargetPatternList << "jboss"
			excludePropertyPatternList << "jboss"
		}	
		if (useTomcat != "true") 
		{
			excludeTargetPatternList << "tomcat"
			excludePropertyPatternList << "tomcat"
		}	
		if (useGrid != "true") 
		{
			excludeTargetPatternList << "grid"
			excludePropertyPatternList << "grid"
		}	
		if (useMaven != "true") 
		{
			excludeTargetPatternList << "maven"
			excludePropertyPatternList << "maven"
		}	
		if (useGuiInstaller != "true") 
		{
			excludeTargetPatternList << "gui-installer"
			excludePropertyPatternList << "gui-installer"
		}	
	}

	private static void createBaseFilteredXmlFile (String fileName)
	{
		/*
		 This method writes the contents of the xml file upto the first target into a buffer that is used to form the begining part of the output file.  This assumes that once the first target is found all lines following are targets. If there are any lines not in between <target> and </target> they will be lost.   All comments should be moved from outside targets to in the body of the targets.  Since BDA controls the contents of the install and build.xml files we can follow these rules to ensure proper filtering.
		*/
		String fileContents = new File(templateDir + "/" + fileName).text

		def cleanFileBuffer =""
		def breakFound = false
		fileContents.eachLine
		{
			// Match the first line containing <target then stop appending lines to the buffer
			if (! breakFound)
			{
				if (it.matches(".*<target.*"))
				{
					breakFound =true
				} else
				{
					 cleanFileBuffer += it + "\n"
				}
			}
		}
		
		fileContentsBuffer = cleanFileBuffer + "\n</project>"
	}
 
	private static void appendFilteredTargets (String fileName)
	{
		/*
		This method parses code blocks between <target> and </target>.  This means that all targets must end with </target> not "/>".  The first part of this method builds a list of include and exclude target names based on the excludePatterns defined above.  The second part then skips targets that are in the exlcude list and also cleans up excluded targets from the depends lists of included targets.  If the the target is include it is appended to the global variable fileContentsBuffer.
		*/
		String fileContents = new File(templateDir + "/" + fileName).text
		targetBuffer = fileContents

		 // Matches a single <target>.*</target>, the *? says match as few as possible, Pattern.DOTALL causes . to match patterns across lines.
		def targetMatcher =  java.util.regex.Pattern.compile(/(<target.*?<\/target>)/, Pattern.DOTALL).matcher(fileContents)
		//build include and exclude target lists.
		while(targetMatcher.find())
		{
			def targetText = targetMatcher.group(1)
			
			// Parse the target name from the code block.
			def targetNamePattern="<target\\s+name=\"([^\"]+)"
			def targetNameMatcher =java.util.regex.Pattern.compile(targetNamePattern, Pattern.DOTALL).matcher(targetText)
			targetNameMatcher.find()
			def targetName = targetNameMatcher.group(1)
			
			/*
			Compare the target name to the excludePattern if it matches add it to the exclude list otherwise add to include list.  Because the exclude list can include many patterns and the target name could match 0 or 1 (or more) patterns, as we loop through the exclude patterns we set a flag if a match is found.  After the loop we determine wether to add it to the exclude or include list based on the flag.
			*/

			def excludeTarget = false
			excludeTargetPatternList.each
			{ excludePattern ->
				if (targetName =~ /${excludePattern}/)
				{
					excludeTarget = true
				//	println "\t${targetName} match ${excludePattern}"
				} else
				{
				//	println "\t${targetName} does not match ${excludePattern}"
				}
			}
			if (excludeTarget == true)
			{
				//println "Excluding ${targetName}"
				targetExcList << targetName
			} else
			{
			//	println "Including ${targetName}"
				targetIncList << targetName
			}
		}
		println "\nTargets Included - ${targetIncList}"
		println "\nTargets Excluded - ${targetExcList}"
		
		// Now that the lists are included we actually process the targets
		targetMatcher.reset()
		while(targetMatcher.find())
		{
			def targetText = targetMatcher.group(1)
			
			// parse out the target name
			def targetNamePattern="<target\\s+name=\"([^\"]+)"
			def targetNameMatcher =java.util.regex.Pattern.compile(targetNamePattern, Pattern.DOTALL).matcher(targetText)
			targetNameMatcher.find()
			def targetName = targetNameMatcher.group(1)

			// Only process the target if it is not in the exclude list
			if (!targetExcList.contains(targetName))
			{
				// Match the depnds of the target
				def targetDependPattern="depends=\"([^\"]+)\""
				def targetDependMatcher =java.util.regex.Pattern.compile(targetDependPattern, Pattern.DOTALL).matcher(targetText)
				// If it is found process it
				if (targetDependMatcher.find())
				{
					def targetDependString = targetDependMatcher.group(1)
					//println "${targetName}\tdepends - ${targetDependString.trim()}"
					def newDepString="depends=\"\n"
					def depChanged = false
					// Loop through each target and if it is in the exclude list then set a changed flag otherwise add it to the new depends string.
					targetDependString.split(',').each
					{ targetDep ->
						def dep = targetDep.trim()
						if (targetExcList.contains(dep))
						{
							depChanged=true
						} else
						{
							newDepString += "\t\t" + dep +",\n "
						}
					}
					// Remove final trailing , from string
					newDepString = newDepString.replaceAll(/,\s*$/,"\n\t\t\"")
					//  If it is changed then update the depends list
					if (depChanged)
					{
						targetText = targetDependMatcher.replaceFirst(newDepString)
					}
				}
				// Write the target (modified or not) to the file Contents by replacing the </project> tag with the target and the tag
				def replaceString = Matcher.quoteReplacement("\n\t" + targetText + "\n</project>")
				fileContentsBuffer = fileContentsBuffer.replaceAll("</project>",replaceString)
				// print debug output for inclded
				println "${fileName.toString()} ${targetName} included"
			} else
			{
				// print debug output for excluded 
				println "${fileName.toString()} ${targetName} EXCLUDED"
			}	
		}
	}
	private static void filterPropertiesXmlFile ()
	{
		/*
		This method filters the non target elememts of the xml file.  Intially it was just properties, but we added mkdir and available tasks also. 
		*/
		def newBuffer = ""
		fileContentsBuffer.eachLine
		{ line ->
			def exclude = false
			if (line.matches(".*<property.*") || 
				line.matches(".*<mkdir.*")||
				line.matches(".*<available.*") ||
				line.matches(".*<.*/>.*")
				)
			{
				excludePropertyPatternList.each
				{ excludeProp ->
					if (line.contains(excludeProp))
					{
						exclude = true
					}
				}
				if (! exclude) { newBuffer += line + "\n"}
			} else
			{
				 newBuffer += line + "\n"
			}
		}
		fileContentsBuffer = newBuffer
	}

	private static void outputXmlFile (String fileName)
	{
		/*
		This methods writes the fileContentsBuffer to a file, but before it it replaces the projectSearchString (bda-blueprints) with the projectReplaceString (from properties file).
		*/
		def tempBuffer1 =""
		def tempBuffer2 =""
		fileContentsBuffer.eachLine { tempBuffer1 += it.replaceAll(projectSearchString1,projectReplaceString) + "\n"}
		tempBuffer1.eachLine { tempBuffer2 += it.replaceAll(projectSearchString2,projectReplaceString) + "\n"}
		
		File outFile = new File(projectBuildDir + "/" + fileName)
		outFile.write(tempBuffer2)
	}
	private static void processXmlFile (String fileName)
	{
		// This method calls all the xml file related methods.

		createBaseFilteredXmlFile(fileName)
		appendFilteredTargets(fileName)
		filterPropertiesXmlFile()
		outputXmlFile(fileName)
	}
	private static void filterPropertiesFile (String fileName)
	{
		/*
		This method filters proeprtis files.  Excludes lines that match the excludePatterns.  Also it replaces the projectSearchString (bda-blueprints) with the projectReplaceString (from properties file).
		*/
		String fileContents = new File(templateDir + "/" + fileName).text
		def newBuffer = ""
		fileContents.eachLine
		{ line ->
			// Matches each exclude pattern against the line, if there is a match a flag is set.
			def exclude = false
			excludePropertyPatternList.each
			{ excludeProp ->
				if (line.contains(excludeProp))
				{
					exclude = true
				}
			}
			// If a match was found in the exclude list the line is excluded 
			if (! exclude) { newBuffer += line + "\n"}
		}
		def tempBuffer1 =""
		def tempBuffer2 =""
		newBuffer.eachLine { tempBuffer1 += it.replaceAll(projectSearchString1,projectReplaceString) + "\n"}
		tempBuffer1.eachLine { tempBuffer2 += it.replaceAll(projectSearchString2,projectReplaceString) + "\n"}
		File outFile = new File(projectBuildDir + "/" + fileName)
		outFile.write(tempBuffer2)
	}
}
