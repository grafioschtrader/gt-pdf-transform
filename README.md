# GT-PDF-Transform
Transform is a desktop JavaFX application that allows to anonymize a large number of PDF files. For example, it can recursively import all PDFs in a file directory. Afterwards the text lines which are not relevant for the import can be removed in a mass processing. If your securities transactions are more or less completely available as PDF files, you can process them with Transform and then import them into [Grafioschtrader](//github.com/grafioschtrader/grafioschtrader).

## Execute GT-PDF-Transform
GT-PDF-Transform comes with an installation program. Depending on the platform the installation is easier or a bit more complicated. The application is not signed, which more or less causes difficulties.

### Ubuntu für AMD64 platform
Download the latest **Ubunutu Build** of [GT-PDF-Transform](//github.com/grafioschtrader/gt-pdf-transform/releases/tag/Ubuntu-latest). In the download directory execute the following command:
```
# Replace XX.XX... with your downloaded build
sudo dpkg -i gt-pdf-transform_XX.XX.XXX-X_amd64.deb
```
For execution use the Desktop or this command `/opt/gt-pdf-transform/bin/GT-PDF-Transform` may work.

#### Remove GT-PDF-Transform from ubuntu
```
sudo dpkg -r gt-pdf-transform
```

### Windows
Download the latest **Windows Build** of [GT-PDF-Transform](//github.com/grafioschtrader/gt-pdf-transform/releases/tag/Windows-latest). Execute the msi installer. Ignore "Windows Protected Your PC" as this application is not signed. Instead, click "more info" and run the installation anyway.

### macOS
Download the latest **macOS Build** of [GT-PDF-Transform](//github.com/grafioschtrader/gt-pdf-transform/releases/tag/macOS-latest). After that, install the downloaded dmg file like the rest of the applications. GT-PDF-Transform requires root privileges to run. Therefore the execution in the **terminal** is the easiest. Start a **terminal** and execute the following command:
```
sudo "/Applications/GT-PDF-Transform.app/Contents/MacOS/GT-PDF-Transform"
```

## Build GT-PDF-Transform
For the build we use [maven-jpackage-template](//github.com/wiverson/maven-jpackage-template). The following text was taken from there

1. Install [OpenJDK Java 16](https://adoptopenjdk.net/) or
   [Oracle Java 16](https://www.oracle.com/java/technologies/javase-downloads.html).
    - Verify by opening a fresh Terminal/Command Prompt and typing `java --version`.
2. Install [Apache Maven 3.8.1](http://maven.apache.org/install.html) or later and make sure it's on your path.
    - Verify this by opening a fresh Terminal/Command Prompt and typing `mvn --version`.
3. macOS: verify XCode is installed and needed agreements accepted.
    - Launch XCode and accept the license, or verify in Terminal with the command `sudo xcodebuild -license`.
5. Windows: install [Wix 3 binaries](https://github.com/wixtoolset/wix3/releases/).
    - Installing Wix via the installer should be sufficient for jpackage to find it.
3. Clone/download this project.
6. Final step: run `mvn clean install` from the root of the project to generate the `target\TestApp.dmg`
   or `target\TestApp.msi` (installer).
    - Note that the actual generated installer will include a version number in the file name
    - For reference, here is a complete run log for [a successful run](docs/sample-run.md).

Because these builds use stripped down JVM images, the
[generated installers are in the 30-40mb range](https://github.com/wiverson/maven-jpackage-template/releases).
