package ch.bubendorf.rupi

import com.beust.jcommander.JCommander

/*
Ein paar Feststellungen:
- Der Name der *.bmp Datei muss dem in der RUPI Datei verwendeten POI-Namen entsprechen. Dieser darf durchaus
  ungleich dem Dateinamen sein.
- Als *.bmp werden auch PNGs akzeptiert - Die Datei muss aber auf .bmp enden.
- Der gleiche POI-Name darf in mehreren RUPI Dateien vorkommen. Die *.bmp muss dann nur einmal vorhanden sein.
- Die Icons / *.bmp werden nach .../Android/data/com.sygic.aura/files/Res/icons/rupi importiert. Dabei wird dem
  Dateinamen ein _ vorangestellt und es wird eine Datei _*.bmp.3d.bmp erzeugt.
- Die RUPI Dateien kommen 1:1 nach .../Android/data/com.sygic.aura/files/Maps/rupi/<Land>.
  Der Dateiname wird dabei durch einen Zeitstempel (Sekunden seit 01.01.1970 * 36000) ersetzt.
 */

fun main(args: Array<String>) {
    val cmdArgs = CommandLineArguments()
    val jCommander = JCommander(cmdArgs)
    jCommander.parse(*args)

    if (cmdArgs.isHelp) {
        jCommander.usage()
        System.exit(1)
    }

    if (!cmdArgs.isValid) {
        System.exit(2)
    }

    cmdArgs.inputFiles.forEach { inputFile ->
        RupiConverter(cmdArgs.name, inputFile, cmdArgs.outputPath).convert()
    }

    System.exit(0)
}

