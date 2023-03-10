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
import androidx.lifecycle.lifecycleScope
import android.util.Base64
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class SearchMealByDatedatesFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
	private lateinit var root: View
	private lateinit var myContext: Context
	private lateinit var model: ModelFacade
	
	private lateinit var mealBean: MealBean
	
	private lateinit var datesTextField: EditText
	private var datesData = ""
	private lateinit var searchMealByDateSpinner: Spinner
	private var allMealdatess: List<String> = ArrayList()
	private lateinit var searchMealByDateButton : Button
	private lateinit var cancelsearchMealByDateButton : Button	
	
	private lateinit var mealIdTextView: TextView
	private lateinit var mealNameTextView: TextView
	private lateinit var caloriesTextView: TextView
	private lateinit var datesTextView: TextView
	private lateinit var imagesImageView: ImageView
	private var imagesData = ""
	var dimages: Bitmap? = null
	private lateinit var analysisTextView: TextView
	private lateinit var userNameTextView: TextView
	
    companion object {
        fun newInstance(c: Context): SearchMealByDatedatesFragment {
            val fragment = SearchMealByDatedatesFragment()
            val args = Bundle()
            fragment.arguments = args
            fragment.myContext = c
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		root = inflater.inflate(R.layout.searchmealbydatedates_layout, container, false)
	    return root
	}
	
	override fun onResume() {
		super.onResume()
		model = ModelFacade.getInstance(myContext)
		mealBean = MealBean(myContext)

		datesTextField = root.findViewById(R.id.searchMealByDateField)	    
		searchMealByDateSpinner = root.findViewById(R.id.searchMealByDateSpinner)
		
		model.allMealDatess.observe( viewLifecycleOwner, androidx.lifecycle.Observer { mealDates ->
					mealDates.let {
						allMealdatess = mealDates
						val searchMealByDateAdapter =
						ArrayAdapter(myContext, android.R.layout.simple_spinner_item, allMealdatess)
						searchMealByDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
						searchMealByDateSpinner.adapter = searchMealByDateAdapter
						searchMealByDateSpinner.onItemSelectedListener = this
					}
				})
					

		searchMealByDateButton = root.findViewById(R.id.searchMealByDateOK)
		searchMealByDateButton.setOnClickListener(this)
		cancelsearchMealByDateButton = root.findViewById(R.id.searchMealByDateCancel)
		cancelsearchMealByDateButton.setOnClickListener(this)
		mealIdTextView = root.findViewById(R.id.searchMealByDatemealIdView)
		mealNameTextView = root.findViewById(R.id.searchMealByDatemealNameView)
		caloriesTextView = root.findViewById(R.id.searchMealByDatecaloriesView)
		datesTextView = root.findViewById(R.id.searchMealByDatedatesView)
	imagesImageView = root.findViewById(R.id.searchMealByDateimagesImageView)
		analysisTextView = root.findViewById(R.id.searchMealByDateanalysisView)
		userNameTextView = root.findViewById(R.id.searchMealByDateuserNameView)
		datesTextField = root.findViewById(R.id.searchMealByDateField)

	}
	
	override fun onItemSelected(parent: AdapterView<*>, v: View?, position: Int, id: Long) {
		if (parent === searchMealByDateSpinner) {
			datesTextField.setText(allMealdatess[position])
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
		R.id.searchMealByDateOK-> {
			searchMealByDateOK()
		}
		R.id.searchMealByDateCancel-> {
			searchMealByDateCancel()
		}
	  }
    }

	private fun searchMealByDateOK() {
		datesData = datesTextField.text.toString()
		mealBean.setDates(datesData)
		
		if (mealBean.isSearchMealError(allMealdatess)) {
			Log.w(javaClass.name, mealBean.errors())
			Toast.makeText(myContext, "Errors: " + mealBean.errors(), Toast.LENGTH_LONG).show()
		} else {
			viewLifecycleOwner.lifecycleScope.launchWhenStarted  {	
					val selectedItem = model.searchMealByDate(datesData)[0]
mealIdTextView.text = selectedItem.mealId.toString()
mealNameTextView.text = selectedItem.mealName.toString()
caloriesTextView.text = selectedItem.calories.toString()
datesTextView.text = selectedItem.dates.toString()
      dimages = try {
					    // convert base64 to bitmap android
					    val decodedString: ByteArray = Base64.decode(selectedItem.images, Base64.DEFAULT)
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
analysisTextView.text = selectedItem.analysis.toString()
userNameTextView.text = selectedItem.userName.toString()
			}
				Toast.makeText(myContext, "search Meal done!", Toast.LENGTH_LONG).show()
		}
	}

	private fun searchMealByDateCancel() {
		mealBean.resetData()
		mealIdTextView.text = ""
		mealNameTextView.text = ""
		caloriesTextView.text = ""
		datesTextView.text = ""
		imagesImageView.setImageResource(0)
		analysisTextView.text = ""
		userNameTextView.text = ""
	}
		 
}
