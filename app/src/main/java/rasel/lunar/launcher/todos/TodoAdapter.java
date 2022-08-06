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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import rasel.lunar.launcher.R;
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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_list, viewGroup, false));
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int i) {
        holder.todoText.setText("●  " + todoList.get(i).getName());

        if(currentFragment instanceof TodoManager) {
            holder.todoText.setOnClickListener(v -> updateDialog(i));
            holder.todoText.setOnLongClickListener(v -> {
                (new UniUtils()).copyToClipboard(fragment.requireActivity(), context, todoList.get(i).getName());
                return true;
            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView todoText;
        ViewHolder(View view) {
            super(view);
            todoText = view.findViewById(R.id.todo_text);
        }
    }

    private void updateDialog(int i) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.requireActivity());
        Objects.requireNonNull(bottomSheetDialog).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View editView = fragment.getLayoutInflater().inflate(R.layout.todo_dialog, null);
        bottomSheetDialog.setContentView(editView);
        bottomSheetDialog.show();

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        Todo todo = todoList.get(i);

        editView.findViewById(R.id.delete_all_confirmation).setVisibility(View.GONE);
        TextInputEditText todoInput = editView.findViewById(R.id.todo_input);
        MaterialButton todoDelete = editView.findViewById(R.id.todo_cancel);
        MaterialButton todoUpdate = editView.findViewById(R.id.todo_ok);

        todoInput.setText(todoList.get(i).getName());
        todoDelete.setText(context.getString(R.string.delete));
        todoDelete.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
        todoUpdate.setText(context.getString(R.string.update));

        todoDelete.setOnClickListener(v -> {
            databaseHandler.deleteTodo(todoList.get(i).getId());
            bottomSheetDialog.dismiss();
            todoManager.refreshList();
        });

        todoUpdate.setOnClickListener(v -> {
            String updatedTodoString = Objects.requireNonNull(todoInput.getText()).toString().trim();
            if(updatedTodoString.length() > 0) {
                todo.setName(updatedTodoString);
                databaseHandler.updateTodo(todo);
                bottomSheetDialog.dismiss();
                todoManager.refreshList();
            } else {
                todoInput.setError(context.getString(R.string.empty_text_field));
            }
        });
    }
}
