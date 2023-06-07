package com.example.dietpro

import android.content.Context
import java.util.*

class FindBMRBean(c: Context) {
    private var model: ModelFacade = ModelFacade.getInstance(c)

    private var user = ""
	private var instanceUser: User? = null
	

    private var errors = ArrayList<String>()

    fun setUser(userx: String) {
        user = userx
    }
    

    fun resetData() {
        user = ""
    }

fun isFindBMRError(): Boolean {
	    errors.clear()
        instanceUser = model.getUserByPK(user)
        if (instanceUser == null) {
            errors.add("user must be a valid User id")
        }
        

	    return errors.isNotEmpty()
	}

    fun errors(): String {
        return errors.toString()
    }

    fun findBMR (): Double {
        return model.findBMR(instanceUser!!)
    }

}
