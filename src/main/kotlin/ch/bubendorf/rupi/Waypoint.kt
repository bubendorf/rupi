package ch.bubendorf.rupi

// Aus der Sygic Dokumentation:
// For each POI the parameters must be arranged in following order:
// longitude | latitude | name | address | phone | fax | web | email | short description | long description

class Waypoint
constructor(longitude: Double,
            latitude: Double,
            val name: String,
            val address: String = "",
            val phone: String = "",
            val fax: String = "",
            val web: String = "",
            val email: String = "",
            val shortDescription: String = "",
            val longDescription: String = "") : Coordinate(longitude, latitude) {
    val longitudeInt = Math.round(longitude * 100000.0).toInt()
    val latitudeInt = Math.round(latitude * 100000.0).toInt()

    constructor(record: List<String>) : this(
            record[0].toDouble(),
            record[1].toDouble(),
            record[2],
            if (record.size > 3) record[3] else "",
            if (record.size > 4) record[4] else "",
            if (record.size > 5) record[5] else "",
            if (record.size > 6) record[6] else "",
            if (record.size > 7) record[7] else "",
            if (record.size > 8) record[8] else "",
            if (record.size > 9) record[9] else "")

    constructor(record: Array<String>) : this(record.toList())


}
