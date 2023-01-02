import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists

//change bucketName to your Google Cloud bucket name (:
const val bucketName = "roboflow_soccer_ball_dataset"
const val annotationFileName = "_annotations.csv"
const val resultFileName = "object_detection.csv"

val buckets = listOf("/test/" to "test", "/train/" to "train", "/valid/" to "valid")

fun main(args: Array<String>) {

    if (args[0].isEmpty()) {
        throw Exception("File path is empty!")
    }

    val path = args[0]

//    create new file if needed
    val glImportFile =
        if (Path.of(path, resultFileName).exists())
            Path.of(path, resultFileName)
        else
            Files.createFile(Path.of(path, resultFileName))
//    clear file
    Files.writeString(Path.of(path, resultFileName), "")

    buckets.forEach { bucket ->
        Files.readString(Path.of(path, bucket.first, annotationFileName)).split("\n")
            .forEachIndexed { index, s ->
//                in my case first line is a data format example
                if (index != 0)
                    Files.writeString(
                        glImportFile,
                        convertString(s, bucket.second).also { println(it) },
                        StandardOpenOption.APPEND
                    )
            }
    }
}

/**
 * Input format: fileName,width,height,class,xmin,ymin,xmax,ymax
 * Output format: mlUse,gcsFilePath,label,X_MIN,Y_MIN,,,X_MAX,Y_MAX,,*
 */

fun convertString(input: String, type: String): String {
    val inputList = input.split(",")
//    TODO: bucketName from arguments
    return "$type,gs://$bucketName/${inputList[0]}," +
            "${inputList[3]}," +
            "${divideZeroWise(inputList[4], inputList[1])}," +
            "${divideZeroWise(inputList[5], inputList[2])},,," +
            "${divideZeroWise(inputList[6], inputList[1])}," +
            "${divideZeroWise(inputList[7], inputList[2])},,\n"
}

fun divideZeroWise(numerator: String, denominator: String): String {
    if (denominator.toInt() == 0) {
        return "0"
    }
    return "${numerator.toFloat() / denominator.toFloat()}"
}