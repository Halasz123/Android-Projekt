package com.example.trafficsigns.ui.fragments.Profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.trafficsigns.R
import com.example.trafficsigns.databinding.FragmentNeuralNetworkBinding
import com.example.trafficsigns.ui.constants.Data
import com.example.trafficsigns.ui.constants.ToastMessage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import java.io.File
import java.io.FileReader
import java.io.InputStream


class NeuralNetworkFragment : Fragment() {

    private lateinit var binding: FragmentNeuralNetworkBinding
    private lateinit var photoFile: File
    private lateinit var imageFromFile: Bitmap



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_neural_network,
                container,
                false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.photo.setOnClickListener {
            startCameraIntent()
        }

        binding.galleryPicture.setOnClickListener {
            checkPermission()
        }

        iterateOnTestDirectory()
        val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.gyalogos00018)

       // Log.d("NEURAL", "Gyalogos")
        classifier(bitmap)
    }

    private fun startCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile("Traffic")

        val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                Data.PACKAGE_FILEPROVIDER_PATH,
                photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (activity?.let { it1 -> takePictureIntent.resolveActivity(it1.packageManager) } != null){
            startActivityForResult(takePictureIntent, CAPTURE_PHOTO_CODE)
        }
        else {
            Toast.makeText(requireContext(), ToastMessage.UNABLE_OPEN_CAMERA, Toast.LENGTH_LONG).show()
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile(fileName, ".jpg", storageDirectory)
    }
    private fun pickGalleryPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else {
                pickGalleryPhoto()
            }
        }
        else {
            pickGalleryPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            var path: String = ""
            when (requestCode) {
                CAPTURE_PHOTO_CODE -> {
                    binding.profilePicture.setImageBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
                    val bitmap: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    classifier(bitmap)
                }
                IMAGE_PICK_CODE -> {
                    binding.profilePicture.setImageURI(data?.data)

                    val pickedImage: Uri = data?.data!!
                    val `is`: InputStream? = context?.contentResolver?.openInputStream(pickedImage)
                    val bitmap = BitmapFactory.decodeStream(`is`)
                    `is`?.close()
                    classifier(bitmap)
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun classifier(bitmap: Bitmap): MutableList<Classifications> {
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(30, 30, ResizeOp.ResizeMethod.BILINEAR))
                .build()

        var tImage = TensorImage(DataType.FLOAT32)
        tImage.load(bitmap)
        tImage = imageProcessor.process(tImage)

        val options = ImageClassifierOptions.builder().setMaxResults(3).build()
        val imageClassifier = ImageClassifier.createFromFileAndOptions(context, "KagleModel.tflite", options)
        var results = imageClassifier.classify(tImage)
//        Log.d("NEURAL", results.toString())
        Log.d("NEURAL-best", "${results[0].categories[0].label}  = ${results[0].categories[0].score} | ${results[0].headIndex}")

        return  results

    }

    private fun iterateOnTestDirectory(){
        var dir = "/data/data/com.example.trafficsigns/files/Test/"
        var reader = FileReader(dir + "Test.csv")
        var csvLines = reader.readLines()
        reader = FileReader(dir + "label43.txt")
        var labels = reader.readLines()
        Log.d("NEURAL", labels.toString())
        var correctId = 0
        csvLines.forEach {
            var line = Pair(it.split(",")[0], it.split(",")[1])
            //Log.d("NEURAL", line.first + " " + line.second)
            var bitmap = BitmapFactory.decodeFile(dir + line.second)
            var resultByModel = classifier(bitmap)
            var resultClassId = labels.indexOf(resultByModel[0].categories[0].label)
            Log.d("NEURAL-TEST", "ClassId: ${line.first}... Result: $resultClassId | ${resultByModel[0].categories[0].label} - ${resultByModel[0].categories[0].score} ")
            Log.d("NEURAL", resultByModel.toString())

            if(resultClassId == line.first.toInt()){
                correctId++
            }
        }
        Log.d("NEURAL", "Ossz: ${csvLines.size}  |  Helyes: $correctId")

    }

}