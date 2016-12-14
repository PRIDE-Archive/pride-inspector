pride-inspector
===============

# About PRIDE Inspector

PRIDE Inspector is a desktop application to visualise and perform first quality assessment on Mass Spectrometry data.

# Quick Download 

[<img src="https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/download.png">](http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/desktop/pride-inspector.zip)

# License

pride-inspector is a PRIDE API licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

# How to cite it:

Perez-Riverol, Yasset, Qing-Wei Xu, Rui Wang, Julian Uszkoreit, Johannes Griss, Aniel Sanchez, Florian Reisinger et al. "PRIDE Inspector Toolsuite: Moving Toward a Universal Visualization Tool for Proteomics Data Standard Formats and Quality Assessment of ProteomeXchange Datasets." Molecular & Cellular Proteomics 15, no. 1 (2016): 305-317 [PDF File](http://www.mcponline.org/content/15/1/305.full.pdf) [Pubmed Record](http://www.ncbi.nlm.nih.gov/pubmed/26545397)
Wang, R., Fabregat, A., Ríos, D., Ovelleiro, D., Foster, J. M., Côté, R. G., ... & Vizcaíno, J. A. (2012). PRIDE Inspector: a tool to visualize and validate MS proteomics data. Nature biotechnology, 30(2), 135-137. [PDF File](http://www.nature.com/nbt/journal/v30/n2/pdf/nbt.2112.pdf), [Pubmed Record](http://www.ncbi.nlm.nih.gov/pubmed/22318026)

# Main Features

* Fast loading of mzML, PRIDE XML and mzIdentML files.
* Search, access and download all PRIDE public database experiments.
* Different views on spectra, chromatogram, protein, peptides and metadata.
* Visualise quantification data for both protein and peptide identifications.
* Experiment summary on key measurements of experiment quality.
* Download additional protein details, such as: protein name, sequence.
* Visualise protein sequences and their peptide/PTM coverage.
* Visualisation for all spectra and chromatograms, including automatic MS2 fragment ion annotations.
* Possibility to perform a quality assessment of the data using a statistical view with different charts.
* User-friendly download facility for private PRIDE experiments.


# Supported File Formats

mzML 1.1
PRIDE XML 2.1
mzIdentML 1.1.0
Peak Files (mgf, ms2, pkl, dta, mzData, mzXML, apl)

**Note**: the tool is still evolving, we are committed to expand the tool and add more features such as protein inference and metadata checklist.

# Getting PRIDE Inspector

## Installation Requirements

* Java: Java JRE 1.7 (or above), which you can download for free [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html). (Note: most computers should have Java installed already).
* Operating System: The current version has been tested on Windows 7, Windows Vista, Linux and Max OS X, it should also work on other platforms. If you come across any problems on your platform, please contact the PRIDE Help Desk.
* Memory: MS dataset can be very large sometimes, in order to get good performance from PRIDE Inspector, we recommend you to have 1G of free memory.

## Launch via Webstart

Click [here](http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/webstart/pride-inspector.jnlp) to launch directly the latest PRIDE Inspector.

*Please note that Mac OS 10.8 (Mountain Lion) users or users of the Google Chrome browser, may have to execute additional steps. Please see FAQ section below if in doubt.*

## Download

You can get the latest PRIDE Inspector from our [Download Section](http://www.ebi.ac.uk/pride/resources/tools/inspector/latest/desktop/pride-inspector.zip), and download pride-inspector-X.Y.zip (where X and Y represent the version of the software). Unzipping the file, creates the following directory structure:

  pride-inspector-X.Y
     pride-inspector-X.Y.jar
     log
     lib
     examples
     config

To start the software, simply double-click the file named pride-inspector-X.Y.jar. If this fails, try to download and install Java 1.7 or above, as explained in the previous section. (The program can also be started from the command line using the following command: java -jar pride-inspector-X.Y.jar.)

The zip file contains also an examples folder with 2 sample files: one in mzML format (mzml-example.mzML) and the other in PRIDE xml format (pride-example.xml) so you can upload them in pride inspector and try the application. There is and additional folder, config, that contains a file called config.props where you can modify the amount of memory assigned to your application (only change if you are trying to view files and is causing the software crash because of a "Out of memory..." exception). The additional 2 directories, lib and log, contain all the java libraries necessary for the application to run and some debugging information if the application crashes.

##Maven Dependency

PRIDE Inspector can be used in Maven projects, you can include the following snippets in your Maven pom file.
 
 ```maven
 <dependency>
   <groupId>uk.ac.ebi.pride.toolsuite</groupId>
   <artifactId>pride-inspector</artifactId>
   <version>x.x.x</version>
 </dependency> 
 ```
 ```maven
 <!-- EBI repo -->
 <repository>
     <id>nexus-ebi-repo</id>
     <url>http://www.ebi.ac.uk/intact/maven/nexus/content/repositories/ebi-repo</url>
 </repository>
 
 <!-- EBI SNAPSHOT repo -->
 <snapshotRepository>
    <id>nexus-ebi-repo-snapshots</id>
    <url>http://www.ebi.ac.uk/intact/maven/nexus/content/repositories/ebi-repo-snapshots</url>
 </snapshotRepository>
```
Note: you need to change the version number to the latest version.

For developers, the latest source code is available from our SVN repository.

# Faqs

## Mac OS X App Gatekeeper Message

If a user downloads the PRIDE Inspector software onto Mac OS X 10.8, they will see a scary warning:

<quote>"PRIDE Inspecor can’t be opened because it is from an unidentified developer"</quote>

![Mac Error](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/mac_error.png)

<string>Solution</strong>

1- Solution 1: To override your security settings and open the app anyway:


    - In the Finder, locate the app you want to open. Don’t use Launchpad to do this. Launchpad doesn’t allow you to access the shortcut menu.

    - Press the Control key, then click the app icon.
 
    - Choose Open from the shortcut menu.

    - Click Open.

The app is saved as an exception to your security settings, and you will be able to open it in the future by double-clicking it, just like any registered app.

2- Solution 2: Permanet solution

  To run unsigned software you need to go into Mac OS X Preferences>Security & Privacy>General and change Allow applications downloaded from Mac App store and identified developers to Anywhere:
  
![Mac Preferences](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/mac_preferences.png)  

# Getting Help

If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: pride-support at ebi.ac.uk (replace at with @).

Please send us your feedback, including error reports, improvement suggestions, new feature requests and any other things you might want to suggest to the PRIDE team.

# Screenshots

Protein View

![Protein View](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/protein_tab.png)

Peptide View

![Peptide View](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/peptide_tab.png)

Spectrum View

![Spectrum View](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/spectrum_tab.png)

Chart View

![Chart View](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/chart_tab.png)

Metadata View

![Metadata View](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/general_tab.png)

Quatification View

![Quatification View](https://raw.githubusercontent.com/PRIDE-Toolsuite/pride-inspector/master/wiki/quantification_tab.png)

# Acknowledgments

Supported by: 

![YourKit](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.


