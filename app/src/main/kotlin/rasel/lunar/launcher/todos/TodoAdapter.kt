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

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import rasel.lunar.launcher.LauncherActivity.Companion.lActivity
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.ListItemBinding
import rasel.lunar.launcher.databinding.TodoDialogBinding
import rasel.lunar.launcher.helpers.Constants.Companion.KEY_TODO_COUNTS
import rasel.lunar.launcher.helpers.Constants.Companion.PREFS_SETTINGS
import rasel.lunar.launcher.helpers.UniUtils.Companion.copyToClipboard
import java.util.*


internal class TodoAdapter(
    private val todoManager: TodoManager?,
    private val context: Context) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val currentFragment = lActivity!!.supportFragmentManager.findFragmentById(R.id.mainFragmentsContainer)
    private val todoList = DatabaseHandler(context).todos

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TodoViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        /*  if current fragment is LauncherHome,
            then return size following the settings value */
        val sharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, 0)
        val numberOfTodos = sharedPreferences.getInt(KEY_TODO_COUNTS, 3)
        return if (currentFragment !is TodoManager) {
            todoList.size.coerceAtMost(numberOfTodos)
        } else {
            todoList.size
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]

        holder.view.itemText.text = "\u25CF  ${todo.name}"

        if (currentFragment is TodoManager) {
            /* multiline texts are enabled for TodoManager */
            holder.view.itemText.isSingleLine = false
            /* launch edit or update dialog on item click */
            holder.view.itemText.setOnClickListener { updateDialog(position) }
            /* copy texts on long click */
            holder.view.itemText.setOnLongClickListener {
                copyToClipboard(context, todo.name)
                true
            }
        } else {
            /* single line text for home screen */
            holder.view.itemText.isSingleLine = true
        }
    }

    inner class TodoViewHolder(var view: ListItemBinding) : RecyclerView.ViewHolder(view.root)

    /* update dialog */
    private fun updateDialog(position: Int) {
        val bottomSheetDialog = BottomSheetDialog(lActivity!!, R.style.BottomSheetDialog)
        val dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        val databaseHandler = DatabaseHandler(context)
        val todo = databaseHandler.todos[position]

        dialogBinding.apply {
            deleteAllConfirmation.visibility = View.GONE
            todoInput.setText(todo.name)
            todoCancel.text = context.getString(R.string.delete)
            todoCancel.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
            todoOk.text = context.getString(R.string.update)
        }

        /* delete the item */
        dialogBinding.todoCancel.setOnClickListener {
            databaseHandler.deleteTodo(todo.id)
            bottomSheetDialog.dismiss()
            todoManager?.refreshList()
        }

        /* update the item */
        dialogBinding.todoOk.setOnClickListener {
            val updatedTodoString = Objects.requireNonNull(dialogBinding.todoInput.text).toString().trim { it <= ' ' }
            if (updatedTodoString.isNotEmpty()) {
                todo.name = updatedTodoString
                databaseHandler.updateTodo(todo)
                bottomSheetDialog.dismiss()
                todoManager?.refreshList()
            } else {
                dialogBinding.todoInput.error = context.getString(R.string.empty_text_field)
            }
        }
    }

}
