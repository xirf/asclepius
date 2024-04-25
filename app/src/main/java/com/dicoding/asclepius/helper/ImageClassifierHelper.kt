package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener?,
    private val scoreThreshold: Float = 0.5f,
    private val maxResults: Int = 3,
    private val modelName: String = "cancer_classification.tflite"
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val numThreadsForTFLite = calculateNumThreadsForTFLite()

        val imageClassifierOptions = createImageClassifierOptions(numThreadsForTFLite)
        createImageClassifier(imageClassifierOptions)
    }

    // Gunakan 2/3 dari jumlah thread yang tersedia
    private fun calculateNumThreadsForTFLite(): Int {
        val availableThreads = Runtime.getRuntime().availableProcessors()
        return (availableThreads * 2) / 3
    }


    private fun createImageClassifierOptions(numThreadsForTFLite: Int): ImageClassifierOptions {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(numThreadsForTFLite)
            .build()

        return ImageClassifierOptions.builder()
            .setScoreThreshold(scoreThreshold)
            .setMaxResults(maxResults)
            .setBaseOptions(baseOptions)
            .build()
    }

    private fun createImageClassifier(options: ImageClassifierOptions) {
        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(context, modelName, options)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleException(e: Exception) {
        val errorMessage = context.getString(R.string.image_classifier_is_not_initialized_yet)
        classifierListener?.onError(errorMessage)
        Log.e("ImageClassifierHelper", e.message.toString())
    }

    fun classifyStaticImage(imageUri: Uri) {
        if (imageClassifier == null)
            setupImageClassifier();

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(loadImage(imageUri)))
        val result = imageClassifier?.classify(tensorImage)
        classifierListener?.onResults(result)
    }

    private fun loadImage(imageUri: Uri): Bitmap {
        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(results: List<Classifications>?)
    }
}