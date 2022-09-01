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

package rasel.lunar.launcher.todos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import rasel.lunar.launcher.helpers.Constants;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final Constants constants = new Constants();

    public DatabaseHandler(Context context) {
        super(context, new Constants().TODO_DATABASE_NAME, null, new Constants().TODO_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTodoTable = "CREATE TABLE " + constants.TODO_TABLE + " (" +
                constants.TODO_COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                constants.TODO_COL_CREATED + " datetime DEFAULT CURRENT_TIMESTAMP," +
                constants.TODO_COLUMN_NAME + " varchar)";
        database.execSQL(createTodoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    protected void addTodo(Todo todo) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(constants.TODO_COLUMN_NAME, todo.getName());
        database.insert(constants.TODO_TABLE, null, contentValues);
    }

    protected void updateTodo(Todo todo) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(constants.TODO_COLUMN_NAME, todo.getName());
        database.update(constants.TODO_TABLE, contentValues, constants.TODO_COLUMN_ID + "=?", new String[]{String.valueOf(todo.getId())});
    }

    protected void deleteTodo(Long todoId) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(constants.TODO_TABLE, constants.TODO_COLUMN_ID + "=?", new String[]{String.valueOf(todoId)});
    }

    protected void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(constants.TODO_TABLE, null, null);
    }

    @SuppressLint("Range")
    public ArrayList<Todo> getTodos() {
        ArrayList<Todo> todoList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor queryResult = database.rawQuery("SELECT * from " + constants.TODO_TABLE, null);
        if (queryResult.moveToFirst()) {
            do {
                Todo todo = new Todo();
                todo.setId(queryResult.getLong(queryResult.getColumnIndex(constants.TODO_COLUMN_ID)));
                todo.setName(queryResult.getString(queryResult.getColumnIndex(constants.TODO_COLUMN_NAME)));
                todoList.add(todo);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return todoList;
    }

    protected boolean todoExists() {
        SQLiteDatabase database = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(database, constants.TODO_TABLE, String.valueOf(1)) > 0;
    }
}
