package com.jwd.lunchvote.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jwd.lunchvote.local.room.entity.FoodEntity

@Dao
interface FoodDao {
    // 음식을 데이터베이스에 추가하기
    @Insert
    fun insertFood(food: FoodEntity): Long

    // 음식의 id로 불러오기
    @Query("SELECT * FROM FoodTable WHERE foodId = :foodId")
    fun getFood(foodId: Long): FoodEntity

    // 음식의 이름으로 불러오기
    @Query("SELECT * FROM FoodTable WHERE name = :name")
    fun getFoodByName(name: String): FoodEntity

    // 모든 음식 불러오기
    @Query("SELECT * FROM FoodTable")
    fun getAllFoods(): List<FoodEntity>

    // 음식 업데이트하기
    @Update
    fun updateFood(food: FoodEntity)

    // 음식의 id로 삭제하기
    @Query("DELETE FROM FoodTable WHERE foodId = :foodId")
    fun deleteFood(foodId: Long)

    // 음식의 이름으로 삭제하기
    @Query("DELETE FROM FoodTable WHERE name = :name")
    fun deleteFoodByName(name: String)

    // 모든 음식 삭제하기
    @Query("DELETE FROM FoodTable")
    fun deleteAllFoods()
}