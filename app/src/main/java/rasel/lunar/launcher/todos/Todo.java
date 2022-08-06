package rasel.lunar.launcher.todos;

public class Todo {

    private long id = -1;
    private String name = "";

    protected final long getId() {
        return this.id;
    }

    protected final void setId(long var) {
        this.id = var;
    }

    protected final String getName() {
        return this.name;
    }

    protected final void setName(String name) {
        this.name = name;
    }
}
