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
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Objects;

import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.ListItemBinding;
import rasel.lunar.launcher.databinding.TodoDialogBinding;
import rasel.lunar.launcher.helpers.Constants;
import rasel.lunar.launcher.helpers.UniUtils;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    ArrayList<Todo> todoList;
    TodoManager todoManager;
    Context context;
    FragmentManager fragmentManager;
    Fragment fragment, currentFragment;

    public TodoAdapter(TodoManager todoManager, ArrayList<Todo> todoList, Context context, FragmentManager fragmentManager, Fragment fragment) {
        this.todoList = todoList;
        this.todoManager = todoManager;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
        currentFragment = fragmentManager.findFragmentById(R.id.main_fragments_container);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        SharedPreferences sharedPreferences = context.getSharedPreferences((new Constants()).SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        int numberOfTodos = sharedPreferences.getInt((new Constants()).SHARED_PREF_SHOW_TODOS, 3);
        if(!(currentFragment instanceof TodoManager)) {
            return Math.min(todoList.size(), numberOfTodos);
        } else {
            return todoList.size();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.view.itemText.setText("●  " + todoList.get(position).getName());

        if(currentFragment instanceof TodoManager) {
            holder.view.itemText.setSingleLine(false);
            holder.view.itemText.setOnClickListener(v -> updateDialog(position));
            holder.view.itemText.setOnLongClickListener(v -> {
                (new UniUtils()).copyToClipboard(fragment.requireActivity(), context, todoList.get(position).getName());
                return true;
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding view;
        ViewHolder(ListItemBinding v) {
            super(v.getRoot());
            view = v;
        }
    }

    private void updateDialog(int i) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.requireActivity());
        Objects.requireNonNull(bottomSheetDialog).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TodoDialogBinding dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(todoManager.getContext()));
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.show();

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        Todo todo = todoList.get(i);

        dialogBinding.deleteAllConfirmation.setVisibility(View.GONE);
        dialogBinding.todoInput.setText(todoList.get(i).getName());
        dialogBinding.todoCancel.setText(context.getString(R.string.delete));
        dialogBinding.todoCancel.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
        dialogBinding.todoOk.setText(context.getString(R.string.update));

        dialogBinding.todoCancel.setOnClickListener(v -> {
            databaseHandler.deleteTodo(todoList.get(i).getId());
            bottomSheetDialog.dismiss();
            todoManager.refreshList();
        });

        dialogBinding.todoOk.setOnClickListener(v -> {
            String updatedTodoString = Objects.requireNonNull(dialogBinding.todoInput.getText()).toString().trim();
            if(updatedTodoString.length() > 0) {
                todo.setName(updatedTodoString);
                databaseHandler.updateTodo(todo);
                bottomSheetDialog.dismiss();
                todoManager.refreshList();
            } else {
                dialogBinding.todoInput.setError(context.getString(R.string.empty_text_field));
            }
        });
    }
}
