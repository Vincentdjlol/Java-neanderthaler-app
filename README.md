$${\color{hotpink}\Huge\textsf{Neanderthaler DNA Comparer}}$$

With the Java Neanderthaler App you can compare your human VCF file with a Neanderthaler VCF file, or with another VCF file of your choice.
Both files must be based on the reference genome GRCh38.
The output shows how many variants the human and the Neanderthaler have after filtering, how many of them are shared and what percentage of the human variants the Neanderthaler also carries.
Optionally the separate variants are shown in the terminal and all results are written to a TSV file with the header of the human VCF.

## What it does
The app reads both VCF files and turns each line into variants. Only variants that the person really carries are kept. Lines that cannot be read are skipped and reported, so one broken line does not stop the program. Variants that were rejected by the variant caller or that have a quality below your threshold are filtered out. The app then compares the human and Neanderthaler variants and finds out which variants they share, which ones belong to only one of them and where both have a different variant at the same position. Finally it prints a summary in your terminal.

## Installation

### 1. Download the project
Download the project from GitHub or clone the repository with `git clone https://github.com/Vincentdjlol/Java-neanderthaler-app.git`

### 2. Build the application
Go to the Java-neanderthaler-app folder and run `.\gradlew.bat shadowJar` in your terminal. On macOS or Linux use `./gradlew shadowJar`.
Gradle downloads all needed dependencies and builds the JAR file. The file is saved as `build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar`.

## Getting started
Try the program with the test files that come with the project.

`java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human test_files/human.vcf --neanderthaler test_files/neanderthaler.vcf`

## What you get back
The terminal always shows a summary with the number of variants per file, the shared variants and the shared percentage. It also shows a table per chromosome and a table per variant type. With `--details` you also see the separate variants, and with `--output` all variants are saved to a TSV file.

The shared percentage is the share of human variants that the Neanderthaler also carries. It is not a measure of Neanderthal ancestry.

## Using the command line
Run the program with `java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human <path>`

| Option | Required | Description |
|---|---|---|
| `--human` | yes | Path to the VCF file of the human |
| `--neanderthaler` | no | Path to the VCF file of the Neanderthaler. Without it the program uses `test_files/neanderthaler.vcf` |
| `--chrom` | no | Only compare one chromosome, for example `21` or `chr21` |
| `--min-qual` | no | Skip variants with a quality lower than this number. The default is 0 |
| `--details` | no | Also show the separate variants in the terminal |
| `--output` | no | Save all variants with their category to this TSV file |

## Help and options
To see all options, run `java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --help`

## Made by
Vincent and Nicole

## Contact
noreply@nicole.com