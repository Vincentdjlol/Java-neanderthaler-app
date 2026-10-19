$${\color{hotpink}\Huge\textsf{Neanderthaler DNA Comparer}}$$

The Java Neanderthaler App is a command-line application that allows for the comparison of genetic variants between a modern human and a Neanderthaler and
gives you insights on the similarity between the DNA entered by the user and the Neanderthal genome.

## What it does
The app reads two VCF files and checks them first, so broken input is caught before anything else happens. It then compares the human and Neanderthaler variants position by position and finds out which variants they share and which ones belong to only one of them. Finally it writes a clear summary to your terminal, and if you want also to a file, so you can see in one glance how much Neanderthal is hiding in the DNA.

## Installation

Step 1: Download the project

Download the project from GitHub or clone the repository using `git clone https://github.com/Vincentdjlol/Java-neanderthaler-app.git`

Step 2: Open and build the application

Navigate to the Java-neanderthaler-app directory and run the following command in your terminal: `.\gradlew.bat shadowJar` (on macOS or Linux use `./gradlew shadowJar`)
Gradle will then download all the needed dependencies and build the JAR file. This file will be saved in `build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar`

## What you give it
Two VCF files, one from a modern human and one from a Neanderthaler. You pass them to the program with `--human` and `--neanderthaler`. If you leave out `--neanderthaler`, the program uses the Neanderthaler file that comes with the project.

## What you get back
A summary with the variants both files share and the variants that only appear in one of them. This is always shown in your terminal and can also be saved to a file with `--output`.

## Getting started
For now use the test files that come with the project.

`java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human test_files/human.vcf --neanderthaler test_files/neanderthaler.vcf`

## Using the command line

To run the program, enter the following into your command line: `java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human <path> --neanderthaler <path>`

--human: is the path to the VCF file for the human. This argument is mandatory.

--neanderthaler: is the path to the VCF file for the neanderthaler. This argument is optional, if you leave it out the program uses `test_files/neanderthaler.vcf`.

You can also use these optional arguments:

--chrom: only compare one chromosome, for example `21` or `chr21`

--min-qual: skip variants with a quality lower than this number

--output: also save all variants to a TSV file


## Help and options
To see all options, run `java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --help`

## Made by
Vincent and Nicole

## Contact
noreply@nicole.com
