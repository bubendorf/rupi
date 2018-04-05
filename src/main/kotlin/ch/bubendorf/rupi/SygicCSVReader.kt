package ch.bubendorf.rupi

import com.opencsv.CSVReader
import java.io.Reader

class SygicCSVReader(val reader: Reader,
                     val linesToSkip: Int = 0,
                     val separator: Char = ',',
                     val quotechar: Char = '"',
                     val escape: Char = '\\') {

    fun readWaypoints(): List<Waypoint> {
        var csvReader: CSVReader? = null

        try {
            csvReader = CSVReader(reader, separator, quotechar, escape, linesToSkip)
            val waypoints = ArrayList<Waypoint>()
            var record: Array<String>? = csvReader.readNext()
            while (record != null) {
//                println(record[0] + " | " + record[1] + " | " + record[2] + " | " + record[3])
                if (record.size >= 3 && record[0].isNotBlank() && record[1].isNotBlank() && record[2].isNotBlank()) {
                    val waypoint = Waypoint(record)
                    waypoints.add(waypoint)
                }
                record = csvReader.readNext()
            }
            csvReader.close()
            return waypoints
        } finally {
            csvReader!!.close()
        }
    }
}
