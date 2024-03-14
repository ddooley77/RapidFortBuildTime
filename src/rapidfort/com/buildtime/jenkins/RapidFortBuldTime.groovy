package rapidfort.com.buildtime.jenkins;
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

public class RapidFortBuldTime {
	
	// Path to the installed CLI script
	static final CLI_SCRIPT_PATH = "/usr/local/bin/rfstub"
	
	// Helper method to check if the CLI script is installed
	static boolean isCliInstalled() {
		return new File(CLI_SCRIPT_PATH).exists()
	}
	
	// Method to download a script from a given URL and execute it
	static void downloadAndInstall(String scriptUrl) {
		try {
			// Download the script content
			def scriptContent = new URL(scriptUrl).text
			
			// Write script content to a temporary file
			def tempScriptFile = Files.createTempFile("temp_script", ".sh")
			Files.write(tempScriptFile, scriptContent.getBytes())
			
			// Execute the script using bash
			def processBuilder = new ProcessBuilder("sudo", "bash", tempScriptFile.toString())
			def process = processBuilder.start()
			
			// Wait for the process to finish
			process.waitFor()
			
			// Print the output of the script execution
			def output = process.inputStream.text
			println "Script output:\n$output"
			
			// Check if there was any error
			if (process.exitValue() != 0) {
				def errorOutput = process.errorStream.text
				println "Error output:\n$errorOutput"
				throw new RuntimeException("Script execution failed with exit code ${process.exitValue()}")
			}
			
		} catch (Exception e) {
			println "Error: ${e.message}"
			throw e
		}
	}
	
	// Method to run 'rfstub' CLI command
	static void runRFStubCommand(String dockerImageTag) {
		if (!isCliInstalled()) {
			throw new IllegalStateException("CLI is not installed. Please call 'downloadAndInstall' method first.")
		}
		
		// Execute 'rfstub' command with docker image tag
		def command = "$CLI_SCRIPT_PATH/rfstub $dockerImageTag"
		executeCommand(command)
	}
	
	// Method to run 'rfharden' CLI command
	static void runRFHardenCommand(String dockerImageTag) {
		if (!isCliInstalled()) {
			throw new IllegalStateException("CLI is not installed. Please call 'downloadAndInstall' method first.")
		}
		
		// Execute 'rfharden' command with docker image tag
		def command = "$CLI_SCRIPT_PATH/rfharden $dockerImageTag"
		executeCommand(command)
	}
	
	// Helper method to execute shell commands
	private static void executeCommand(String command) {
		try {
			def process = command.execute()
			process.waitFor()
			def output = process.text
			println "Command output:\n$output"
			
			if (process.exitValue() != 0) {
				throw new RuntimeException("Command execution failed with exit code ${process.exitValue()}")
			}
		} catch (Exception e) {
			println "Error: ${e.message}"
			throw e
		}
	}
	
}
