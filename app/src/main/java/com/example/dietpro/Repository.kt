package com.example.dietpro

import kotlinx.coroutines.flow.Flow

class Repository : MealRepository  {

    private val mealDao: MealEntityDao by lazy { DietproApplication.database.mealDao() }

    val allMeals: Flow<List<MealEntity>> = mealDao.listMeals()

    val allMealmealIds: Flow<List<String>> = mealDao.listMealmealIds()
    val allMealmealNames: Flow<List<String>> = mealDao.listMealmealNames()
    val allMealcaloriess: Flow<List<Double>> = mealDao.listMealcaloriess()
    val allMealdatess: Flow<List<String>> = mealDao.listMealdatess()
    val allMealimagess: Flow<List<String>> = mealDao.listMealimagess()
    val allMealanalysiss: Flow<List<String>> = mealDao.listMealanalysiss()
    val allMealuserNames: Flow<List<String>> = mealDao.listMealuserNames()

    //Create
    override suspend fun createMeal(meal: MealEntity) {
        mealDao.createMeal(meal)
    }

    //Read
    override suspend fun listMeal(): List<MealEntity> {
        return mealDao.listMeal()
    }

    //Update
    override suspend fun updateMeal(meal: MealEntity) {
        mealDao.updateMeal(meal)
    }

    //Delete all Meals
    override suspend fun deleteMeals() {
       mealDao.deleteMeals()
    }

    //Delete a Meal
	override suspend fun deleteMeal(mealId: String) {
	   mealDao.deleteMeal(mealId)
    }
    
     //Search with live data
     override fun searchByMealmealId (searchQuery: String): Flow<List<MealEntity>>  {
         return mealDao.searchByMealmealId(searchQuery)
     }
     
     //Search with live data
     override fun searchByMealmealName (searchQuery: String): Flow<List<MealEntity>>  {
         return mealDao.searchByMealmealName(searchQuery)
     }
     
     //Search with live data
     override fun searchByMealcalories (searchQuery: Double): Flow<List<MealEntity>>  {
         return mealDao.searchByMealcalories(searchQuery)
     }
     
     //Search with live data
     override fun searchByMealdates (searchQuery: String): Flow<List<MealEntity>>  {
         return mealDao.searchByMealdates(searchQuery)
     }
     
     //Search with live data
     override fun searchByMealimages (searchQuery: String): Flow<List<MealEntity>>  {
         return mealDao.searchByMealimages(searchQuery)
     }
     
     //Search with live data
     override fun searchByMealanalysis (searchQuery: String): Flow<List<MealEntity>>  {
         return mealDao.searchByMealanalysis(searchQuery)
     }
     
     //Search with live data
     override fun searchByMealuserName (searchQuery: String): Flow<List<MealEntity>>  {
         return mealDao.searchByMealuserName(searchQuery)
     }
     

    //Search with suspend
     override suspend fun searchByMealmealId2 (mealId: String): List<MealEntity> {
          return mealDao.searchByMealmealId2(mealId)
     }
	     
    //Search with suspend
     override suspend fun searchByMealmealName2 (mealName: String): List<MealEntity> {
          return mealDao.searchByMealmealName2(mealName)
     }
	     
    //Search with suspend
     override suspend fun searchByMealcalories2 (calories: Double): List<MealEntity> {
          return mealDao.searchByMealcalories2(calories)
     }
	     
    //Search with suspend
     override suspend fun searchByMealdates2 (dates: String): List<MealEntity> {
          return mealDao.searchByMealdates2(dates)
     }
	     
    //Search with suspend
     override suspend fun searchByMealimages2 (images: String): List<MealEntity> {
          return mealDao.searchByMealimages2(images)
     }
	     
    //Search with suspend
     override suspend fun searchByMealanalysis2 (analysis: String): List<MealEntity> {
          return mealDao.searchByMealanalysis2(analysis)
     }
	     
    //Search with suspend
     override suspend fun searchByMealuserName2 (userName: String): List<MealEntity> {
          return mealDao.searchByMealuserName2(userName)
     }
	     


	//Add remove Relationship
     override suspend fun addUsereatsMeal(userName: String, mealId: String) {
          return  mealDao.addUsereatsMeal(userName, mealId)
     }
        
     override suspend fun removeUsereatsMeal(userName: String, mealId: String) {
          return  mealDao.removeUsereatsMeal(userName, mealId)
     }
        
}
