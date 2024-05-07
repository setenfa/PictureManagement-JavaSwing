package function;
//
public class TreeNodeEntity {
    private String name;
    private String path;
    public TreeNodeEntity() {
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public String toString(){
        return name;
    }
}
