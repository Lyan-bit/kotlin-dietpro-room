package com.example.dietpro

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealEntityDao {
    //LiveData
    //Read (list entity)
    @Query("SELECT * FROM mealTable")
    fun listMeals(): Flow<List<MealEntity>>

    //Read (list mealId)
	@Query("SELECT mealId FROM mealTable")
	fun listMealmealIds (): Flow<List<String>>
    //Read (list mealName)
	@Query("SELECT mealName FROM mealTable")
	fun listMealmealNames (): Flow<List<String>>
    //Read (list calories)
	@Query("SELECT calories FROM mealTable")
	fun listMealcaloriess (): Flow<List<Double>>
    //Read (list dates)
	@Query("SELECT dates FROM mealTable")
	fun listMealdatess (): Flow<List<String>>
    //Read (list images)
	@Query("SELECT images FROM mealTable")
	fun listMealimagess (): Flow<List<String>>
    //Read (list analysis)
	@Query("SELECT analysis FROM mealTable")
	fun listMealanalysiss (): Flow<List<String>>
    //Read (list userName)
	@Query("SELECT userName FROM mealTable")
	fun listMealuserNames (): Flow<List<String>>

	//Suspend
    //Create
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createMeal (meal: MealEntity)

    //Read (list entity with suspend)
    @Query("SELECT * FROM mealTable")
    suspend fun listMeal(): List<MealEntity>

    //Update
    @Update
    suspend fun updateMeal (meal: MealEntity)

    //Delete all records
    @Query("DELETE FROM mealTable")
    suspend fun deleteMeals ()

    //Delete a single record by PK
    @Query("DELETE FROM mealTable WHERE mealId = :id")
    suspend fun deleteMeal (id: String)
    
    //Search with live data
	@Query("SELECT * FROM  mealTable WHERE mealId LIKE :searchQuery ")
	fun searchByMealmealId(searchQuery: String): Flow<List<MealEntity>>
	@Query("SELECT * FROM  mealTable WHERE mealName LIKE :searchQuery ")
	fun searchByMealmealName(searchQuery: String): Flow<List<MealEntity>>
	@Query("SELECT * FROM  mealTable WHERE calories LIKE :searchQuery ")
	fun searchByMealcalories(searchQuery: Double): Flow<List<MealEntity>>
	@Query("SELECT * FROM  mealTable WHERE dates LIKE :searchQuery ")
	fun searchByMealdates(searchQuery: String): Flow<List<MealEntity>>
	@Query("SELECT * FROM  mealTable WHERE images LIKE :searchQuery ")
	fun searchByMealimages(searchQuery: String): Flow<List<MealEntity>>
	@Query("SELECT * FROM  mealTable WHERE analysis LIKE :searchQuery ")
	fun searchByMealanalysis(searchQuery: String): Flow<List<MealEntity>>
	@Query("SELECT * FROM  mealTable WHERE userName LIKE :searchQuery ")
	fun searchByMealuserName(searchQuery: String): Flow<List<MealEntity>>

    //Search with suspend
    @Query("SELECT * FROM  mealTable WHERE mealId LIKE :searchQuery")
	suspend fun searchByMealmealId2(searchQuery: String): List<MealEntity>
    @Query("SELECT * FROM  mealTable WHERE mealName LIKE :searchQuery")
	suspend fun searchByMealmealName2(searchQuery: String): List<MealEntity>
    @Query("SELECT * FROM  mealTable WHERE calories LIKE :searchQuery")
	suspend fun searchByMealcalories2(searchQuery: Double): List<MealEntity>
    @Query("SELECT * FROM  mealTable WHERE dates LIKE :searchQuery")
	suspend fun searchByMealdates2(searchQuery: String): List<MealEntity>
    @Query("SELECT * FROM  mealTable WHERE images LIKE :searchQuery")
	suspend fun searchByMealimages2(searchQuery: String): List<MealEntity>
    @Query("SELECT * FROM  mealTable WHERE analysis LIKE :searchQuery")
	suspend fun searchByMealanalysis2(searchQuery: String): List<MealEntity>
    @Query("SELECT * FROM  mealTable WHERE userName LIKE :searchQuery")
	suspend fun searchByMealuserName2(searchQuery: String): List<MealEntity>

    @Query("UPDATE mealTable SET userName = :userName WHERE mealId LIKE :mealId")
    suspend fun addUsereatsMeal(userName: String, mealId: String)
    @Query("UPDATE mealTable SET userName = :userName WHERE mealId LIKE :mealId")
    suspend fun removeUsereatsMeal(userName: String, mealId: String)
}
