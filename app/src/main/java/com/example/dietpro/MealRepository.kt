package com.example.dietpro

import kotlinx.coroutines.flow.Flow

interface MealRepository {
    //Read
    suspend fun listMeal(): List<MealEntity>

    //Create
    suspend fun createMeal(meal: MealEntity)

    //Update
    suspend fun updateMeal(meal: MealEntity)

    //Delete All Meals
    suspend fun deleteMeals()


    //Delete a Meal by PK
	suspend fun deleteMeal(id: String)
	    
    //Search with live data
    fun searchByMealmealId(searchQuery: String): Flow<List<MealEntity>>
    //Search with live data
    fun searchByMealmealName(searchQuery: String): Flow<List<MealEntity>>
    //Search with live data
    fun searchByMealcalories(searchQuery: Double): Flow<List<MealEntity>>
    //Search with live data
    fun searchByMealdates(searchQuery: String): Flow<List<MealEntity>>
    //Search with live data
    fun searchByMealimages(searchQuery: String): Flow<List<MealEntity>>
    //Search with live data
    fun searchByMealanalysis(searchQuery: String): Flow<List<MealEntity>>
    //Search with live data
    fun searchByMealuserName(searchQuery: String): Flow<List<MealEntity>>

    //Search with suspend
    suspend fun searchByMealmealId2(searchQuery: String): List<MealEntity>
    suspend fun searchByMealmealName2(searchQuery: String): List<MealEntity>
    suspend fun searchByMealcalories2(searchQuery: Double): List<MealEntity>
    suspend fun searchByMealdates2(searchQuery: String): List<MealEntity>
    suspend fun searchByMealimages2(searchQuery: String): List<MealEntity>
    suspend fun searchByMealanalysis2(searchQuery: String): List<MealEntity>
    suspend fun searchByMealuserName2(searchQuery: String): List<MealEntity>

    //Add remove Relationship
    suspend fun addUsereatsMeal(userName: String, mealId: String)
    //Add remove Relationship
    suspend fun removeUsereatsMeal(userName: String, mealId: String)
}
