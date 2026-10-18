The Java Neanderthaler App is a command-line application that allows for the comparison of genetic variants between a modern human and a Neanderthaler and
gives you insides on the similarity between the DNA entered by the user and the Neanderthal genome. 

**Installation**

Step 1: Download the project

Download the project from GitHub or clone the repository using "git clone https://github.com/Vincentdjlol/Java-neanderthaler-app.git"

Step 2: Open and build the application

Navigate to the Java-neanderthaler-app directory and run the following command in your terminal: ".\gradlew.bat shadowJar"
Gradle will then download all the needed dependencies and build the JAR file. This file will be saved in "build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar"

To run the program, enter the following into your command line: "java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human test_files/human.vcf --neanderthaler test_files/neanderthaler.vcf"

**Usage**

To run the program, enter the following into your command line: "java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human --neanderthaler"

--human: is the path to the VCF file for the human.

--neanderthaler: is the path to the VCF file for the neanderthaler.

Both of these arguments are mandatory.
