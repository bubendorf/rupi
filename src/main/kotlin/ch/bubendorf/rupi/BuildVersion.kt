package ch.bubendorf.rupi

class BuildVersion {

    companion object {
        fun getBuildVersion(): String {
            return BuildVersion::class.java.`package`.implementationVersion ?: "dev"
        }
    }
}


