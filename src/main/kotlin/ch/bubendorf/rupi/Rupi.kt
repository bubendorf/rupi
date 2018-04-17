package ch.bubendorf.rupi

import com.beust.jcommander.JCommander
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.concurrent.Executors

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
- Die *.rupi Dateien können auch direkt ins .../rupi/<Land> Verzeichnis kopiert werden. Der Dateiname
  ist beliebig.
 */

private val LOGGER = LoggerFactory.getLogger(RupiConverter::class.java.simpleName)

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

    val tasks = cmdArgs.inputFiles.map { inputFile ->
        Callable {
            if (Files.exists(Paths.get(inputFile))) {
                RupiConverter(cmdArgs.name, inputFile, cmdArgs.outputPath, cmdArgs.encoding).convert()
            } else {
                LOGGER.error("File $inputFile does not exist - Ignoring")
            }
        }
    }.toList()

    var numberOfTasks = cmdArgs.tasks
    if (numberOfTasks <= 0) {
        numberOfTasks = Runtime.getRuntime().availableProcessors()
    }

    val executorService = Executors.newFixedThreadPool(numberOfTasks)
    executorService.invokeAll(tasks)
    executorService.shutdown()

    System.exit(0)
}

