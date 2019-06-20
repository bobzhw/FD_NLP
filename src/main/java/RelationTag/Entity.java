package RelationTag;


/**
 * Created by zhouwei on 2019/6/19.
 */
public class Entity {

    private String className;
    private String individual;

    public Entity(String className, String individual) {
        this.className = className;
        this.individual = individual;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }
}
