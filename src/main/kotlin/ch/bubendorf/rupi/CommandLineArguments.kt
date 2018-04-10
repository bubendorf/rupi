package ch.bubendorf.rupi

import com.beust.jcommander.Parameter
import org.slf4j.LoggerFactory
import java.io.File
import java.util.ArrayList

class CommandLineArguments {

    private val LOGGER = LoggerFactory.getLogger(CommandLineArguments::class.java.simpleName)

    @Parameter(names = arrayOf("-h", "--help"), help = true)
    var isHelp: Boolean = false

    /*@Parameter(names = arrayOf("-v", "--verbose"))
    var isVerbose: Boolean = false*/

    @Parameter(names = arrayOf("-o", "--outputPath"), description = "Output path", required = false)
    var outputPath = "."

    @Parameter(names = arrayOf("-n", "--name"), description = "name", required = false)
    var name = ""

    @Parameter(names = arrayOf("-c", "--count"), description = "Split outout file after that many waypoints", required = false)
    var count = 1000

    /*@Parameter(names = arrayOf("-t", "--tasks"), description = "Number of parallel tasks", required = false)
    var tasks = -1*/

    @Parameter(description = "input files")
    var inputFiles = ArrayList<String>()

    val isValid: Boolean
        get() {
            // Check if the output path exists
            val outputPathFile = File(outputPath)
            if (!outputPathFile.exists() || !outputPathFile.isDirectory) {
                LOGGER.error("Output path '$outputPath' does not exist or is not a directory!")
                return false
            }
            return true
        }

}
