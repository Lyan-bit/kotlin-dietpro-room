package com.example.dietpro

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.ArrayList
import android.graphics.Bitmap
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream

class ModelFacade private constructor(context: Context) {

    private val assetManager: AssetManager = context.assets
    private var fileSystem: FileAccessor
    private var imageClassifier: ImageClassifier


    private var currentUser: UserVO? = null
    private var currentUsers: ArrayList<UserVO> = ArrayList()

    init {
        //init
        ModelPreferencesManager.with(context, "UserDATA")
        currentUser = getUser()
        fileSystem = FileAccessor(context)
        imageClassifier = ImageClassifier(context)
    }

    companion object {
        private val repository by lazy { Repository() }
        private var instance: ModelFacade? = null
        fun getInstance(context: Context): ModelFacade {
            return instance ?: ModelFacade(context)
        }
    }


    val allMeals: LiveData<List<MealEntity>> = repository.allMeals.asLiveData()

    val allMealMealIds: LiveData<List<String>> = repository.allMealmealIds.asLiveData()
    val allMealMealNames: LiveData<List<String>> = repository.allMealmealNames.asLiveData()
    val allMealCaloriess: LiveData<List<Double>> = repository.allMealcaloriess.asLiveData()
    val allMealDatess: LiveData<List<String>> = repository.allMealdatess.asLiveData()
    val allMealImagess: LiveData<List<String>> = repository.allMealimagess.asLiveData()
    val allMealAnalysiss: LiveData<List<String>> = repository.allMealanalysiss.asLiveData()
    val allMealUserNames: LiveData<List<String>> = repository.allMealuserNames.asLiveData()
    private var currentMeal: MealEntity? = null
    private var currentMeals: List<MealEntity> = ArrayList()

    fun searchByMealmealId(searchQuery: String): LiveData<List<MealEntity>>  {
        return repository.searchByMealmealId(searchQuery).asLiveData()
    }

    fun searchByMealmealName(searchQuery: String): LiveData<List<MealEntity>>  {
        return repository.searchByMealmealName(searchQuery).asLiveData()
    }

    fun searchByMealcalories(searchQuery: Double): LiveData<List<MealEntity>>  {
        return repository.searchByMealcalories(searchQuery).asLiveData()
    }

    fun searchByMealdates(searchQuery: String): LiveData<List<MealEntity>>  {
        return repository.searchByMealdates(searchQuery).asLiveData()
    }

    fun searchByMealimages(searchQuery: String): LiveData<List<MealEntity>>  {
        return repository.searchByMealimages(searchQuery).asLiveData()
    }

    fun searchByMealanalysis(searchQuery: String): LiveData<List<MealEntity>>  {
        return repository.searchByMealanalysis(searchQuery).asLiveData()
    }

    fun searchByMealuserName(searchQuery: String): LiveData<List<MealEntity>>  {
        return repository.searchByMealuserName(searchQuery).asLiveData()
    }


    fun getMealByPK(value: String): Flow<Meal> {
        val res: Flow<List<MealEntity>> = repository.searchByMealmealId(value)
        return res.map { meal ->
            val itemx = Meal.createByPKMeal(value)
            if (meal.isNotEmpty()) {
                itemx.mealId = meal[0].mealId
            }
            if (meal.isNotEmpty()) {
                itemx.mealName = meal[0].mealName
            }
            if (meal.isNotEmpty()) {
                itemx.calories = meal[0].calories
            }
            if (meal.isNotEmpty()) {
                itemx.dates = meal[0].dates
            }
            if (meal.isNotEmpty()) {
                itemx.images = meal[0].images
            }
            if (meal.isNotEmpty()) {
                itemx.analysis = meal[0].analysis
            }
            if (meal.isNotEmpty()) {
                itemx.userName = meal[0].userName
            }
            itemx
        }
    }

    suspend fun createMeal(x: MealEntity) {
        repository.createMeal(x)
        currentMeal = x
    }

    suspend fun editMeal(x: MealEntity) {
        repository.updateMeal(x)
        currentMeal = x
    }

    fun setSelectedMeal(x: MealEntity) {
        currentMeal = x
    }

    suspend fun deleteMeal(id: String) {
        repository.deleteMeal(id)
        currentMeal = null
    }

    suspend fun searchMealByDate(dates: String) : ArrayList<Meal> {
        currentMeals = repository.searchByMealdates2(dates)
        var itemsList = ArrayList<Meal>()
        for (x in currentMeals.indices) {
            val vo: MealEntity = currentMeals[x]
            val itemx = Meal.createByPKMeal(vo.mealId)
            itemx.mealId = vo.mealId
            itemx.mealName = vo.mealName
            itemx.calories = vo.calories
            itemx.dates = vo.dates
            itemx.images = vo.images
            itemx.analysis = vo.analysis
            itemx.userName = vo.userName
            itemsList.add(itemx)
        }
        return itemsList
    }


    fun getUser() : UserVO? {
        currentUsers.clear()
        currentUser = ModelPreferencesManager.get<User>("KEYUser")?.let { UserVO(it) }
        currentUser?.let { currentUsers.add(0, it) }
        return currentUser
    }

    fun createUser(x: UserVO) {
        ModelPreferencesManager.put(x, "KEYUser")
        currentUser = x
        currentUser?.let { currentUsers.add(0, it) }
    }
    fun setSelectedUser(x: UserVO) {
        currentUser = x
    }

    fun findTotalConsumedCaloriesByDate(meals: ArrayList<Meal>, user: User, dates: String): Double {
        var result = 0.0
        var totalConsumedCalories: Double
        totalConsumedCalories  = 0.0
        for (meal in meals) {
            if (meal.userName == user.userName && meal.dates == dates) {
                totalConsumedCalories  = totalConsumedCalories + meal.calories
            }
        }
        user.totalConsumedCalories  = totalConsumedCalories
        persistUser (user)
        result  = totalConsumedCalories
        return result
    }

    fun findTargetCalories(user: User): Double {
        var result = 0.0
        user.targetCalories  = user.calculateTargetCalories()
        persistUser (user)
        result  = user.targetCalories
        return result
    }

    fun findBMR(user: User): Double {
        var result = 0.0
        user.bmr  = user.calculateBMR()
        persistUser (user)
        result  = user.bmr
        return result
    }

    fun caloriesProgress(user: User): Double {
        var result = 0.0
        var progress: Double
        progress  = (user.totalConsumedCalories / user.targetCalories) * 100
        persistUser (user)
        result  = progress
        return result
    }

    suspend fun addUsereatsMeal(userName: String, mealId: String) {
        repository.addUsereatsMeal(userName,mealId)
        currentUser = getUserByPK(userName)?.let { UserVO(it) }
    }

    suspend fun removeUsereatsMeal(userName: String, mealId: String) {
        repository.removeUsereatsMeal("NULL", mealId)
        currentUser = getUserByPK(userName)?.let { UserVO(it) }
    }

    suspend fun imageRecognition(meal: Meal ,images: Bitmap): String {
        val result = imageClassifier.recognizeImage(images)
        meal.analysis = result[0].title  +": " + result[0].confidence
        persistMeal(meal)
        return result[0].title  +": " + result[0].confidence
    }


    suspend fun listMeal(): List<MealEntity> {
        currentMeals = repository.listMeal()
        return currentMeals
    }

    suspend fun listAllMeal(): ArrayList<Meal> {
        currentMeals = repository.listMeal()
        var res = ArrayList<Meal>()
        for (x in currentMeals.indices) {
            val vo: MealEntity = currentMeals[x]
            val itemx = Meal.createByPKMeal(vo.mealId)
            itemx.mealId = vo.mealId
            itemx.mealName = vo.mealName
            itemx.calories = vo.calories
            itemx.dates = vo.dates
            itemx.images = vo.images
            itemx.analysis = vo.analysis
            itemx.userName = vo.userName
            res.add(itemx)
        }
        return res
    }

    suspend fun stringListMeal(): List<String> {
        currentMeals = repository.listMeal()
        val res: ArrayList<String> = ArrayList()
        for (x in currentMeals.indices) {
            res.add(currentMeals[x].toString())
        }
        return res
    }

    suspend fun getMealByPK2(value: String): Meal? {
        val res: List<MealEntity> = repository.searchByMealmealId2(value)
        return if (res.isEmpty()) {
            null
        } else {
            val vo: MealEntity = res[0]
            val itemx = Meal.createByPKMeal(value)
            itemx.mealId = vo.mealId
            itemx.mealName = vo.mealName
            itemx.calories = vo.calories
            itemx.dates = vo.dates
            itemx.images = vo.images
            itemx.analysis = vo.analysis
            itemx.userName = vo.userName
            itemx
        }
    }

    suspend fun retrieveMeal(value: String): Meal? {
        return getMealByPK2(value)
    }

    suspend fun allMealMealIds(): ArrayList<String> {
        currentMeals = repository.listMeal()
        val res: ArrayList<String> = ArrayList()
        for (meal in currentMeals.indices) {
            res.add(currentMeals[meal].mealId)
        }
        return res
    }

    fun setSelectedMeal(i: Int) {
        if (i < currentMeals.size) {
            currentMeal = currentMeals[i]
        }
    }

    fun getSelectedMeal(): MealEntity? {
        return currentMeal
    }

    suspend fun persistMeal(x: Meal) {
        val vo = MealEntity(x.mealId, x.mealName, x.calories, x.dates, x.images, x.analysis, x.userName)
        repository.updateMeal(vo)
        currentMeal = vo
    }

    fun listUser(): ArrayList<UserVO> {
        getUser()
        return currentUsers
    }

    fun listAllUser(): ArrayList<User> {
        currentUsers = listUser()
        var res = ArrayList<User>()
        for (x in currentUsers.indices) {
            val vo: UserVO = currentUsers[x]
            val itemx = User.createByPKUser(vo.getUserName())
            itemx.userName = vo.getUserName()
            itemx.gender = vo.getGender()
            itemx.heights = vo.getHeights()
            itemx.weights = vo.getWeights()
            itemx.activityLevel = vo.getActivityLevel()
            itemx.age = vo.getAge()
            itemx.targetCalories = vo.getTargetCalories()
            itemx.totalConsumedCalories = vo.getTotalConsumedCalories()
            itemx.bmr = vo.getBmr()
            res.add(itemx)
        }
        return res
    }

    fun stringListUser(): ArrayList<String> {
        currentUsers = listUser()
        val res: ArrayList<String> = ArrayList()
        for (x in currentUsers.indices) {
            res.add(currentUsers[x].toString())
        }
        return res
    }

    fun getUserByPK(value: String): User? {
        val res: ArrayList<UserVO> = listUser()
        return if (res.isEmpty()) {
            null
        } else {
            val vo: UserVO = res[0]
            val itemx = User.createByPKUser(value)
            itemx.userName = vo.getUserName()
            itemx.gender = vo.getGender()
            itemx.heights = vo.getHeights()
            itemx.weights = vo.getWeights()
            itemx.activityLevel = vo.getActivityLevel()
            itemx.age = vo.getAge()
            itemx.targetCalories = vo.getTargetCalories()
            itemx.totalConsumedCalories = vo.getTotalConsumedCalories()
            itemx.bmr = vo.getBmr()
            itemx
        }
    }

    fun retrieveUser(value: String): User? {
        return getUserByPK(value)
    }

    fun allUserUserNames(): ArrayList<String> {
        currentUsers = listUser()
        val res: ArrayList<String> = ArrayList()
        for (user in currentUsers.indices) {
            res.add(currentUsers[user].getUserName())
        }
        return res
    }

    fun setSelectedUser(i: Int) {
        if (i < currentUsers.size) {
            currentUser = currentUsers[i]
        }
    }

    fun getSelectedUser(): UserVO? {
        return currentUser
    }

    fun persistUser(x: User) {
        val vo = UserVO(x)
        createUser(vo)
        currentUser = vo
    }



}
