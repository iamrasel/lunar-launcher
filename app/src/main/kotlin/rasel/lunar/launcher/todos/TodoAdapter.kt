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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import rasel.lunar.launcher.R
import rasel.lunar.launcher.databinding.ListItemBinding
import rasel.lunar.launcher.databinding.TodoDialogBinding
import rasel.lunar.launcher.helpers.Constants
import rasel.lunar.launcher.helpers.UniUtils
import java.util.*


internal class TodoAdapter(
    private val todoList: ArrayList<Todo>,
    private val todoManager: TodoManager,
    private val fragmentActivity: FragmentActivity,
    private val context: Context) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val currentFragment: Fragment?

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TodoViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        /*  if current fragment is LauncherHome,
            then return size following the settings value */
        val constants = Constants()
        val sharedPreferences = context.getSharedPreferences(constants.PREFS_SETTINGS, Context.MODE_PRIVATE)
        val numberOfTodos = sharedPreferences.getInt(constants.KEY_TODO_COUNTS, 3)
        return if (currentFragment !is TodoManager) {
            todoList.size.coerceAtMost(numberOfTodos)
        } else {
            todoList.size
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TodoViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.view.itemText.text = "\u25CF  " + todoList[position].name

        if (currentFragment is TodoManager) {
            /* multiline texts are enabled for TodoManager */
            holder.view.itemText.isSingleLine = false
            /* launch edit or update dialog on item click */
            holder.view.itemText.setOnClickListener { updateDialog(position) }
            /* copy texts on long click */
            holder.view.itemText.setOnLongClickListener {
                UniUtils().copyToClipboard(fragmentActivity, context, todoList[position].name)
                true
            }
        } else {
            /* single line text for home screen */
            holder.view.itemText.isSingleLine = true
        }
    }

    class TodoViewHolder(var view: ListItemBinding) : RecyclerView.ViewHolder(
        view.root
    )

    /* update dialog */
    private fun updateDialog(i: Int) {
        val bottomSheetDialog = BottomSheetDialog(fragmentActivity)
        val dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(todoManager.context))
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        val databaseHandler = DatabaseHandler(context)
        val todo = todoList[i]

        dialogBinding.deleteAllConfirmation.visibility = View.GONE
        dialogBinding.todoInput.setText(todoList[i].name)
        dialogBinding.todoCancel.text = context.getString(R.string.delete)
        dialogBinding.todoCancel.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
        dialogBinding.todoOk.text = context.getString(R.string.update)

        /* delete the item */
        dialogBinding.todoCancel.setOnClickListener {
            databaseHandler.deleteTodo(todoList[i].id)
            bottomSheetDialog.dismiss()
            todoManager.refreshList()
        }

        /* update the item */
        dialogBinding.todoOk.setOnClickListener {
            val updatedTodoString = Objects.requireNonNull(dialogBinding.todoInput.text).toString().trim { it <= ' ' }
            if (updatedTodoString.isNotEmpty()) {
                todo.name = updatedTodoString
                databaseHandler.updateTodo(todo)
                bottomSheetDialog.dismiss()
                todoManager.refreshList()
            } else {
                dialogBinding.todoInput.error = context.getString(R.string.empty_text_field)
            }
        }
    }

    init {
        val fragmentManager = fragmentActivity.supportFragmentManager
        currentFragment = fragmentManager.findFragmentById(R.id.main_fragments_container)
    }

}
