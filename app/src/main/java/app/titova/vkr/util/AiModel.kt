package app.titova.vkr.util
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AiModel(context: Context, modelPath: String) {

    private val interpreter: Interpreter

    init {
        // Загружаем модель
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val inputStream = assetFileDescriptor.createInputStream()
        val modelData = inputStream.readBytes()
        val byteBuffer = ByteBuffer.allocateDirect(modelData.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelData)
            rewind()
        }
        interpreter = Interpreter(byteBuffer)
    }

    // Метод для изменения размера и масштабирования изображения
    private fun processImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // Создаём ByteBuffer для хранения масштабированных пикселей
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3) // 224x224, 3 канала (RGB), 4 байта на float
        byteBuffer.order(ByteOrder.nativeOrder())

        // Масштабируем пиксели в диапазон [0, 1]
        val intValues = IntArray(224 * 224)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        for (pixelValue in intValues) {
            val r = (pixelValue shr 16 and 0xFF) / 255.0f
            val g = (pixelValue shr 8 and 0xFF) / 255.0f
            val b = (pixelValue and 0xFF) / 255.0f

            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }

    // Метод для инференса
    fun runInference(bitmap: Bitmap): String {
        // Обрабатываем изображение
        val inputBuffer = processImage(bitmap)

        // Подготовка буфера для вывода, размер и тип данных зависят от модели
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1), org.tensorflow.lite.DataType.FLOAT32)

        // Выполняем инференс
        interpreter.run(inputBuffer, outputBuffer.buffer.rewind())

        // Получаем результат
        val outputValue = outputBuffer.floatArray[0]
        return if (outputValue > 0.5) "Helmet detected" else "No Helmet detected"
    }
}
