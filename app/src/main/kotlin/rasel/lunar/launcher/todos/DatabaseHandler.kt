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
import rasel.lunar.launcher.helpers.Constants.Companion.TODO_COLUMN_CREATED
import rasel.lunar.launcher.helpers.Constants.Companion.TODO_COLUMN_ID
import rasel.lunar.launcher.helpers.Constants.Companion.TODO_COLUMN_NAME
import rasel.lunar.launcher.helpers.Constants.Companion.TODO_DATABASE_NAME
import rasel.lunar.launcher.helpers.Constants.Companion.TODO_DATABASE_VERSION
import rasel.lunar.launcher.helpers.Constants.Companion.TODO_TABLE_NAME
import java.util.ArrayList


internal class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(context, TODO_DATABASE_NAME, null, TODO_DATABASE_VERSION) {

    /* create database */
    override fun onCreate(database: SQLiteDatabase) {
        val createTodoTable = "CREATE TABLE " + TODO_TABLE_NAME + " (" +
                TODO_COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                TODO_COLUMN_CREATED + " datetime DEFAULT CURRENT_TIMESTAMP," +
                TODO_COLUMN_NAME + " varchar)"
        database.execSQL(createTodoTable)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}

    /* add new todo entry */
    fun addTodo(todo: Todo) {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TODO_COLUMN_NAME, todo.name)
        database.insert(TODO_TABLE_NAME, null, contentValues)
    }

    /* update or edit existing todo */
    fun updateTodo(todo: Todo) {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TODO_COLUMN_NAME, todo.name)
        database.update(
            TODO_TABLE_NAME,
            contentValues,
            "$TODO_COLUMN_ID=?",
            arrayOf(todo.id.toString())
        )
    }

    /* delete a single todo */
    fun deleteTodo(todoId: Long) {
        writableDatabase.delete(TODO_TABLE_NAME,
            "$TODO_COLUMN_ID=?", arrayOf(todoId.toString()))
    }

    /* delete all existing todos at once */
    fun deleteAll() {
        writableDatabase.delete(TODO_TABLE_NAME, null, null)
    }

    @get:SuppressLint("Range")
    val todos: ArrayList<Todo>
        get() {
            val todoList = ArrayList<Todo>()
            val queryResult =
                readableDatabase.rawQuery("SELECT * from $TODO_TABLE_NAME", null)

            if (queryResult.moveToFirst()) {
                do {
                    val todo = Todo()
                    todo.id = queryResult.getLong(queryResult.getColumnIndex(TODO_COLUMN_ID))
                    todo.name = queryResult.getString(queryResult.getColumnIndex(TODO_COLUMN_NAME))
                    todoList.add(todo)
                } while (queryResult.moveToNext())
            }

            queryResult.close()
            return todoList
        }

    /* check if any item exists in the database */
    val isTodoExists: Boolean get() {
        return DatabaseUtils.queryNumEntries(readableDatabase, TODO_TABLE_NAME, 1.toString()) > 0
    }

}
