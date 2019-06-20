package RelationTag;


/**
 * Created by zhouwei on 2019/6/19.
 *
 */
public class Relation {

    private String startName;
    private String endName;
    private String relationString;

    public String getRelationStringChineseName() {
        return relationStringChineseName;
    }

    public void setRelationStringChineseName(String relationStringChineseName) {
        this.relationStringChineseName = relationStringChineseName;
    }

    private String relationStringChineseName;

    public Relation(String startName, String relationString, String endName) {
        this.startName = startName;
        this.relationString = relationString;
        this.endName = endName;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getRelationString() {
        return relationString;
    }

    public void setRelationString(String relationString) {
        this.relationString = relationString;
    }
}
