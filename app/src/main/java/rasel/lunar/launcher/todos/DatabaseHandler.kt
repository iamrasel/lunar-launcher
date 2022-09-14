/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.todos

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.annotation.SuppressLint
import android.content.Context
import android.database.DatabaseUtils
import rasel.lunar.launcher.helpers.Constants
import java.util.ArrayList

internal class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(context, Constants().TODO_DATABASE_NAME, null, Constants().TODO_DATABASE_VERSION) {
    
    override fun onCreate(database: SQLiteDatabase) {
        val createTodoTable = "CREATE TABLE " + Constants().TODO_TABLE + " (" +
                Constants().TODO_COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                Constants().TODO_COL_CREATED + " datetime DEFAULT CURRENT_TIMESTAMP," +
                Constants().TODO_COLUMN_NAME + " varchar)"
        database.execSQL(createTodoTable)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}

    fun addTodo(todo: Todo) {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constants().TODO_COLUMN_NAME, todo.name)
        database.insert(Constants().TODO_TABLE, null, contentValues)
    }

    fun updateTodo(todo: Todo) {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constants().TODO_COLUMN_NAME, todo.name)
        database.update(
            Constants().TODO_TABLE,
            contentValues,
            Constants().TODO_COLUMN_ID + "=?",
            arrayOf(todo.id.toString())
        )
    }

    fun deleteTodo(todoId: Long) {
        val database = writableDatabase
        database.delete(Constants().TODO_TABLE, Constants().TODO_COLUMN_ID + "=?", arrayOf(todoId.toString()))
    }

    fun deleteAll() {
        val database = writableDatabase
        database.delete(Constants().TODO_TABLE, null, null)
    }

    @get:SuppressLint("Range")
    val todos: ArrayList<Todo>
        get() {
            val todoList = ArrayList<Todo>()
            val database = readableDatabase
            val queryResult = database.rawQuery("SELECT * from " + Constants().TODO_TABLE, null)
            if (queryResult.moveToFirst()) {
                do {
                    val todo = Todo()
                    todo.id = queryResult.getLong(queryResult.getColumnIndex(Constants().TODO_COLUMN_ID))
                    todo.name = queryResult.getString(queryResult.getColumnIndex(Constants().TODO_COLUMN_NAME))
                    todoList.add(todo)
                } while (queryResult.moveToNext())
            }
            queryResult.close()
            return todoList
        }

    fun todoExists(): Boolean {
        val database = readableDatabase
        return DatabaseUtils.queryNumEntries(database, Constants().TODO_TABLE, 1.toString()) > 0
    }
}