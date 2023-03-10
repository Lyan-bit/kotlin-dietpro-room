package com.example.dietpro

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import java.lang.Exception
import java.util.ArrayList
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import java.util.*

class EditMealFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
	private lateinit var root: View
	private lateinit var myContext: Context
	private lateinit var model: ModelFacade

	private lateinit var mealBean: MealBean

	private lateinit var editMealSpinner: Spinner
	private var allMealmealIds: List<String> = ArrayList()
	private lateinit var searchMealButton : Button
	private lateinit var editMealButton : Button
	private lateinit var cancelMealButton : Button

	private lateinit var mealIdTextField: EditText
	private var mealIdData = ""
	private lateinit var mealNameTextField: EditText
	private var mealNameData = ""
	private lateinit var caloriesTextField: EditText
	private var caloriesData = ""
	private lateinit var datesTextField: EditText
	private var datesData = ""
	private lateinit var imagesImageView: ImageView
	private var imagesData = ""
	var dimages: Bitmap? = null
	private lateinit var analysisTextView: TextView
	private var analysisData = ""
	private lateinit var userNameTextField: EditText
	private var userNameData = ""

	val logTag = "MLImageHelper"
	val captureImageActivityRequestCode = 1034
	val pickImageActivityRequestCode = 1064
	val requestReadExternalStorage = 2031
	lateinit var photoFile: File

	lateinit var buttonPick: Button
	lateinit var buttonCapture: Button


	companion object {
		fun newInstance(c: Context): EditMealFragment {
			val fragment = EditMealFragment()
			val args = Bundle()
			fragment.arguments = args
			fragment.myContext = c
			return fragment
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		root = inflater.inflate(R.layout.editmeal_layout, container, false)
		model = ModelFacade.getInstance(myContext)
		mealBean = MealBean(myContext)
		editMealSpinner = root.findViewById(R.id.crudMealSpinner)

		model.allMealMealIds.observe( viewLifecycleOwner, androidx.lifecycle.Observer { mealId ->
			mealId.let {
				allMealmealIds = mealId
				val editMealAdapter =
					ArrayAdapter(myContext, android.R.layout.simple_spinner_item, allMealmealIds)
				editMealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				editMealSpinner.adapter = editMealAdapter
				editMealSpinner.onItemSelectedListener = this
			}
		})

		searchMealButton = root.findViewById(R.id.crudMealSearch)
		searchMealButton.setOnClickListener(this)
		editMealButton = root.findViewById(R.id.crudMealOK)
		editMealButton.setOnClickListener(this)
		cancelMealButton = root.findViewById(R.id.crudMealCancel)
		cancelMealButton.setOnClickListener(this)

		mealIdTextField = root.findViewById(R.id.crudMealmealIdField)
		mealNameTextField = root.findViewById(R.id.crudMealmealNameField)
		caloriesTextField = root.findViewById(R.id.crudMealcaloriesField)
		datesTextField = root.findViewById(R.id.crudMealdatesField)
		imagesImageView = root.findViewById(R.id.crudMealimagesImageView)
		analysisTextView = root.findViewById(R.id.crudMealanalysisView)
		userNameTextField = root.findViewById(R.id.crudMealuserNameField)

		buttonPick = root.findViewById(R.id.buttonPickPhoto)
		buttonPick.setOnClickListener(this)

		buttonCapture = root.findViewById(R.id.buttonTakePhoto)
		buttonCapture.setOnClickListener(this)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
				myContext,
				Manifest.permission.READ_EXTERNAL_STORAGE
			) != PackageManager.PERMISSION_GRANTED
		) {
			requestPermissions(
				arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
				requestReadExternalStorage
			)
		}

		return root
	}

	override fun onItemSelected(parent: AdapterView<*>, v: View?, position: Int, id: Long) {
		if (parent === editMealSpinner) {
			mealIdTextField.setText(allMealmealIds[position])
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {
		//onNothingSelected
	}

	override fun onClick(v: View?) {
		val imm = myContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		try {
			imm.hideSoftInputFromWindow(v?.windowToken, 0)
		} catch (e: Exception) {
			e.message
		}

		when (v?.id) {
			R.id.crudMealSearch-> {
				crudMealSearch()
			}
			R.id.crudMealOK-> {
				crudMealOK()
			}
			R.id.crudMealCancel-> {
				crudMealCancel()
			}
			R.id.buttonPickPhoto-> {
				onPickImage()
			}
			R.id.buttonTakePhoto-> {
				onTakeImage()
			}
		}
	}

	private fun crudMealSearch() {
		mealIdData = mealIdTextField.text.toString()
		mealBean.setMealId(mealIdData)

		if (mealBean.isSearchMealIdError(allMealmealIds)) {
			Log.w(javaClass.name, mealBean.errors())
			Toast.makeText(myContext, "Errors: " + mealBean.errors(), Toast.LENGTH_LONG).show()
		} else {
			viewLifecycleOwner.lifecycleScope.launchWhenStarted  {
				val selectedItem = model.getMealByPK2(mealIdData)
				mealIdTextField.setText(selectedItem?.mealId.toString())
				mealNameTextField.setText(selectedItem?.mealName.toString())
				caloriesTextField.setText(selectedItem?.calories.toString())
				datesTextField.setText(selectedItem?.dates.toString())
				dimages = try {
					// convert base64 to bitmap android
					val decodedString: ByteArray = Base64.decode(selectedItem?.images, Base64.DEFAULT)
					val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
					decodedByte
				}
				catch (e: Exception) {
					e.message
					null
				}
				viewLifecycleOwner.lifecycleScope.launchWhenStarted {
					imagesImageView.setImageBitmap(dimages)
				}
				analysisTextView.text = selectedItem?.analysis.toString()
				userNameTextField.setText(selectedItem?.userName.toString())

				Toast.makeText(myContext, "search Meal done!", Toast.LENGTH_LONG).show()

			}
		}
	}

	private fun crudMealOK() {
		if (imagesImageView.getDrawable() != null) {
			//Convert image to bitmap
			val bitmap = (imagesImageView.getDrawable() as BitmapDrawable).getBitmap()
			val stream = ByteArrayOutputStream()
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
			val image = stream.toByteArray()

			// Android Bitmap to Base64 String
			val encoded = Base64.encodeToString(image, Base64.DEFAULT)

			mealIdData = mealIdTextField.text.toString()
			mealBean.setMealId(mealIdData)
			mealNameData = mealNameTextField.text.toString()
			mealBean.setMealName(mealNameData)
			caloriesData = caloriesTextField.text.toString()
			mealBean.setCalories(caloriesData)
			datesData = datesTextField.text.toString()
			mealBean.setDates(datesData)
			imagesData = encoded
			mealBean.setImages(imagesData)
			analysisData = analysisTextView.text.toString()
			mealBean.setAnalysis(analysisData)
			userNameData = userNameTextField.text.toString()
			mealBean.setUserName(userNameData)

			if (mealBean.isEditMealError(allMealmealIds)) {
				Log.w(javaClass.name, mealBean.errors())
				Toast.makeText(myContext, "Errors: " + mealBean.errors(), Toast.LENGTH_LONG).show()
			} else {
				viewLifecycleOwner.lifecycleScope.launchWhenStarted  {
					mealBean.editMeal()
					Toast.makeText(myContext, "Meal editd!", Toast.LENGTH_LONG).show()

				}
			}
		} else {
			Toast.makeText(myContext, "Please select or take an image!", Toast.LENGTH_SHORT).show()
		}
	}

	private fun crudMealCancel() {
		mealBean.resetData()
		mealIdTextField.setText("")
		mealNameTextField.setText("")
		caloriesTextField.setText("")
		datesTextField.setText("")
		imagesImageView.setImageResource(0)
		analysisTextView.text = ""
		userNameTextField.setText("")
	}

	fun onTakeImage() {
		// create Intent to take a picture and return control to the calling application
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		// Create a File reference for future access
		photoFile = getPhotoFileUri(SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg")

		// wrap File object into a content provider
		// required for API >= 24
		// See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
		val fileProvider = FileProvider.getUriForFile(
			myContext, "com.example.dietpro",
			photoFile)
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

		// If you call startActivityForResult() using an intent that no app can handle, your app will crash.
		// So as long as the result is not null, it's safe to use the intent.
		if (intent.resolveActivity(myContext.getPackageManager()) != null) {
			// Start the image capture intent to take photo
			activity?.startActivityForResult(intent, captureImageActivityRequestCode)
		}
	}

	// Returns the File for a photo stored on disk given the fileName
	fun getPhotoFileUri(fileName: String): File {
		// Get safe storage directory for photos
		// Use `getExternalFilesDir` on Context to access package-specific directories.
		// This way, we don't need to request external read/write runtime permissions.
		val mediaStorageDir: File =
			File(myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), logTag)

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
			Log.d(logTag, "failed to create directory")
		}

		// Return the file target for the photo based on filename
		return File(mediaStorageDir.path + File.separator + fileName)
	}

	/**
	 * getCapturedImage():
	 * Decodes and crops the captured image from camera.
	 */
	private fun getCapturedImage(): Bitmap {
		// Get the dimensions of the View
		val targetW = imagesImageView.width
		val targetH = imagesImageView.height

		var bmOptions = BitmapFactory.Options()
		bmOptions.inJustDecodeBounds = true

		BitmapFactory.decodeFile(photoFile.absolutePath)
		val photoW = bmOptions.outWidth
		val photoH = bmOptions.outHeight
		val scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

		bmOptions = BitmapFactory.Options()
		bmOptions.inJustDecodeBounds = false
		bmOptions.inSampleSize = scaleFactor
		bmOptions.inMutable = true
		return BitmapFactory.decodeFile(photoFile.absolutePath, bmOptions)
	}

	private fun rotateIfRequired(bitmap: Bitmap) {
		try {
			val exifInterface = ExifInterface(
				photoFile.absolutePath
			)
			val orientation = exifInterface.getAttributeInt(
				ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_UNDEFINED
			)
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				rotateImage(bitmap, 90f)
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				rotateImage(bitmap, 180f)
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				rotateImage(bitmap, 270f)
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	/**
	 * Rotate the given bitmap.
	 */
	private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
		val matrix = Matrix()
		matrix.postRotate(angle)
		return Bitmap.createBitmap(
			source, 0, 0, source.width, source.height,
			matrix, true
		)
	}

	fun onPickImage() {
		// create Intent to take a picture and return control to the calling application
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"

		// If you call startActivityForResult() using an intent that no app can handle, your app will crash.
		// So as long as the result is not null, it's safe to use the intent.
		if (intent.resolveActivity(myContext.getPackageManager()) != null) {
			// Start the image capture intent to take photo
			startActivityForResult(intent, pickImageActivityRequestCode)
		}
	}

	protected fun loadFromUri(photoUri: Uri?): Bitmap? {
		var image: Bitmap? = null
		try {
			image = if (Build.VERSION.SDK_INT > 27) {
				val source = ImageDecoder.createSource(
					myContext.getContentResolver(),
					photoUri!!
				)
				ImageDecoder.decodeBitmap(source)
			} else {
				MediaStore.Images.Media.getBitmap(myContext.getContentResolver(), photoUri)
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return image
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == captureImageActivityRequestCode) {
			if (resultCode == Activity.RESULT_OK) {
				val bitmap = getCapturedImage()
				rotateIfRequired(bitmap)
				imagesImageView.setImageBitmap(bitmap)
			} else { // Result was a failure
				Toast.makeText(myContext, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
			}
		} else if (requestCode == pickImageActivityRequestCode) {
			if (resultCode == Activity.RESULT_OK) {
				val takenImage = loadFromUri(data?.data)
				imagesImageView.setImageBitmap(takenImage)
			} else { // Result was a failure
				Toast.makeText(myContext, "Picture wasn't selected!", Toast.LENGTH_SHORT).show()
			}
		}
	}

}
