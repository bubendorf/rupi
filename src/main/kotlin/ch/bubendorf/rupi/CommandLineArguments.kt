package ch.bubendorf.rupi

import com.beust.jcommander.Parameter
import org.slf4j.LoggerFactory
import java.io.File
import java.util.ArrayList

class CommandLineArguments {

    private val logger = LoggerFactory.getLogger(CommandLineArguments::class.java.simpleName)

    @Parameter(names = ["-h", "--help"], help = true)
    var isHelp: Boolean = false

    /*@Parameter(names = arrayOf("-v", "--verbose"))
    var isVerbose: Boolean = false*/

    @Parameter(names = ["-o", "--outputPath"], description = "Output path", required = false)
    var outputPath = "."

    @Parameter(names = ["-n", "--name"], description = "Category name. Defaults to the name of the input file", required = false)
    var name = ""

    @Parameter(names = ["-e", "--encoding"], description = "Encoding to use", required = false)
    var encoding = "iso-8859-1"

    @Parameter(names = ["-t", "--tasks"], description = "Number of parallel tasks", required = false)
    var tasks = -1

    @Parameter(names = ["-r", "--remark"], description = "Remark", required = false)
    var remark = "Rupi Creator Version ${BuildVersion.getBuildVersion()} by Markus Bubendorf"

    @Parameter(description = "input files")
    var inputFiles = ArrayList<String>()

    val isValid: Boolean
        get() {
            // Check if the output path exists
            val outputPathFile = File(outputPath)
            if (!outputPathFile.exists() || !outputPathFile.isDirectory) {
                logger.error("Output path '$outputPath' does not exist or is not a directory!")
                return false
            }
            return true
        }

}
