package com.karebo2.teamapp.roomdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface mainbodyDao {

    @Query("Select * from mainbody")
    fun getAllMainBody():List<mainbody>

    @Query("DELETE FROM mainbody")
    fun deleteMainBody()

    @Insert
    fun addMainBody(mainbody: mainbody)

    @Update
    fun updateMainBody(mainbody: mainbody)
}