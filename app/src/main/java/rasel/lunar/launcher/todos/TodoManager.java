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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import dev.chrisbanes.insetter.Insetter;
import rasel.lunar.launcher.R;
import rasel.lunar.launcher.databinding.TodoDialogBinding;
import rasel.lunar.launcher.databinding.TodoManagerBinding;

public class TodoManager extends Fragment {

    private TodoManagerBinding binding;
    private Context context;
    private DatabaseHandler databaseHandler;
    private final Todo todo = new Todo();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = TodoManagerBinding.inflate(inflater, container, false);

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.getRoot());

        context = requireActivity().getApplicationContext();
        databaseHandler = new DatabaseHandler(context);
        binding.todos.setLayoutManager(new LinearLayoutManager(context));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.deleteAll.setOnClickListener(v -> deleteAllDialog());
        binding.addNew.setOnClickListener(v -> addNewDialog());
    }

    protected void refreshList() {
        binding.todos.setAdapter(new TodoAdapter(this, databaseHandler.getTodos(), context, requireActivity().getSupportFragmentManager(), this));
    }

    private void deleteAllDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        TodoDialogBinding dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(getContext()));
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.show();

        if(!databaseHandler.todoExists()) {
            dialogBinding.todoOk.setEnabled(false);
        }

        dialogBinding.todoInput.setVisibility(View.GONE);
        dialogBinding.todoOk.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));

        dialogBinding.todoCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        dialogBinding.todoOk.setOnClickListener(v -> {
            databaseHandler.deleteAll();
            bottomSheetDialog.dismiss();
            refreshList();
        });
    }

    private void addNewDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        Objects.requireNonNull(bottomSheetDialog).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TodoDialogBinding dialogBinding = TodoDialogBinding.inflate(LayoutInflater.from(getContext()));
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.show();

        dialogBinding.deleteAllConfirmation.setVisibility(View.GONE);
        dialogBinding.todoInput.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(dialogBinding.todoInput, InputMethodManager.SHOW_IMPLICIT);

        dialogBinding.todoCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        dialogBinding.todoOk.setOnClickListener(v -> {
            String todoString = Objects.requireNonNull(dialogBinding.todoInput.getText()).toString().trim();
            if(todoString.length() > 0) {
                todo.setName(todoString);
                databaseHandler.addTodo(todo);
                bottomSheetDialog.dismiss();
                refreshList();
            } else {
                dialogBinding.todoInput.setError(getString(R.string.empty_text_field));
            }
        });
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }
}
