# Exam Call SP

The structure of this repository is the following:
  - "Client" contains the code of the modified client;
  - "Server" contains the code of the modified server.
  - "Client/ToConvertImages" is the only directory, for security purposes, where putting images to convert and it contains samples images
  - "Client/ConvertedImages" contains the converted images received by the server
  
Notes:
  - for testing and logging purposes arguments are specified in "ConversionRequest.launch" and "Converter.launch" files, respectively for client and server
  - if you want to start the client tests just Run as JUnit Test the class "ClientTest.java" in /test/myClientTest
  - if you want to change client arguments set the "org.eclipse.jdt.launching.PROGRAM_ARGUMENTS" value to "argument1 argument2 argument3 ..." in the "ConversionRequest.launch" file
  - if you want to modify the logging level just set the value ".level=INFO" of "logging.properties" files in "resources" folder to desired level