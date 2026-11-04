$${\color{hotpink}\Huge\textsf{Neanderthaler DNA Comparer}}$$

With the Java Neanderthaler App you can compare your human VCF file with a Neanderthaler VCF file, or with another VCF file of your choice.
Both files must be based on the reference genome GRCh38.
The output shows how many variants the human and the Neanderthaler have after filtering, how many of them are shared and what percentage of the human variants the Neanderthaler also carries.
Optionally the separate variants are shown in the terminal and all results are written to a TSV file with the header of the human VCF.

## What it does
The app reads both VCF files and turns each line into variants. Positions where the genotype (GT) is 0/0 or ./. are skipped, because the person does not carry an alternative allele there. When a line has more than one alternative allele, each allele that the person carries becomes its own variant. Lines that cannot be read are skipped and reported, so one broken line does not stop the program.

After that both files go through the same filter. Variants that were rejected by the variant caller (FILTER is not PASS or .) or that have a QUAL value below your threshold are removed. Only the chromosomes 1 to 22, X, Y and MT are kept.

The app then compares the human and Neanderthaler variants and finds out which variants they share, which ones belong to only one of them and where both have a different variant at the same position. Finally it prints a summary in your terminal.

## Requirements
- Java 17 or newer
- About 5 GB of free memory for large VCF files

## Installation

### 1. Download the project
Download the project from GitHub or clone the repository with `git clone https://github.com/Vincentdjlol/Java-neanderthaler-app.git`

### 2. Build the application
Go to the Java-neanderthaler-app folder and run `.\gradlew.bat shadowJar` in your terminal. On macOS or Linux use `./gradlew shadowJar`.
Gradle downloads all needed dependencies and builds the JAR file. The file is saved as `build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar`.

### 3. Get the Neanderthaler VCF
The full Neanderthaler file is too large for GitHub and is not included. The Neanderthal variant data was obtained from the UCSC Genome Browser (hg38), using the Neanderthal track under Variation and Repeats. Download it from the [UCSC Genome Browser Table Browser](https://genome.ucsc.edu/cgi-bin/hgTables) and give the path with `--neanderthaler`. Without this option the app uses `test_files/neanderthal_example.vcf`, which only holds 40 variants on chromosome 1 and is meant for testing.

## Getting started
Try the program with the example file that comes with the project.

`java -Xmx5g -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human test_files/snpEff_example.vcf`

`-Xmx5g` gives Java 5 GB of memory. The app loads the whole VCF into memory, so large files need this. You can lower the number if your computer has less RAM.

## What you get back
The terminal always shows a summary with the number of variants per file, the shared variants and the shared percentage. It also shows a table per chromosome and a table per variant type (SNP, insertion, deletion and other). With `--details` you also see the separate variants, and with `--output` all variants are saved to a TSV file.

The shared percentage is the share of human variants that the Neanderthaler also carries. It is not a measure of Neanderthal ancestry.

## Using the command line
Run the program with `java -Xmx5g -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --human <path>`

| Option | Required | Description |
|---|---|---|
| `--human` | yes | Path to the VCF file of the human |
| `--neanderthaler` | no | Path to the VCF file of the Neanderthaler. Without it the program uses the small example file `test_files/neanderthal_example.vcf` |
| `--chrom` | no | Only compare one chromosome, for example `21` or `chr21` |
| `--min-qual` | no | Skip variants with a QUAL value lower than this number. The QUAL value is the sixth column of your VCF. The default is 0 |
| `--details` | no | Also show the separate variants in the terminal |
| `--output` | no | Save all variants with their category to this TSV file |


## Help and options
To see all options, run `java -jar build/libs/Java-neanderthaler-app-1.0-SNAPSHOT-all.jar --help`

## Made by
Vincent and Nicole

## Contact
For questions or problems, open an issue on the GitHub page of this project!