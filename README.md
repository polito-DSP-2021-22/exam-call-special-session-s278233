# Exam Call SP

The structure of this repository is the following:
  - "Client" contains the code of the modified client;
  - "Server" contains the code of the modified server.
  - "Client/ToConvertImages" is the only directory, for security purposes, where putting images to convert and it contains samples images
  - "Client/ConvertedImages" contains the converted images received by the server
  
## Installation method#1:
  - from eclipse select "Checkout projects from git", follow the default import wizard but select "Search for nested projects" option
  
## Installation method#2:
  - download "exam-call-special-session-s278233-main.zip" from github
  - from Eclipse, import extracted zip with "File->Import->Existing Projects into Workspace" wizard and select "Search for nested projects" option
  
## Run method#1:
  - select "Run as Java Application" for both "Converter.java" and "ConversionRequest.java" classes
  
## Run method#2:
  - open cmd in "Server" folder and type
  
  ```
  java -jar server.jar
  ```
  
  - open cmd in "Client" folder and type
  
  ```
  java -jar client.jar arg1 arg2 arg3
  ```
  
###### Notes:
  - i set the UTF-8 encoding for all classes and runnable to UTF-8 in order to avoid incompatibility between OSs, however if you try running ClientTest from Windows you could notice some errors because the name of a file written in the test contains characters with a different encoding. If it happens just do the following:
  
     - in Eclipse, in Window->Preferences->General->Content Types, set UTF-8 as the default encoding for all content types
     
     - in Eclipse, in Window->Preferences->General->Workspace, set Text file encoding to Other : UTF-8
  	 
  - for testing and logging purposes arguments are specified in "ConversionRequest.launch" and "Converter.launch" files, respectively for client and server
  - if you want to start the client tests just Run as JUnit Test the class "ClientTest.java" in /test/myClientTest
  - if you want to change client arguments set the "org.eclipse.jdt.launching.PROGRAM_ARGUMENTS" value to "argument1 argument2 argument3 ..." in the "ConversionRequest.launch" file
  - if you want to modify the logging level just set the value ".level=INFO" of "logging.properties" files in "resources" folder to desired level