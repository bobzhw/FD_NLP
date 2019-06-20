package RelationTag;


import javax.print.DocFlavor;
import java.util.List;

/**
 * Created by Administrator on 2019/6/18 0018.
 */
public class AimData {

    private String line;  //要完成组合的句子
    private List<Entity> entities;  //每个句子中的实例
    private List<String> results;

    public AimData(String line, List<String> results,List<Entity> entities) {
        this.line = line;
        this.results = results;
        this.entities=entities;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public List<Entity> getEntities()
    {
        return entities;
    }
    public String getLine()
    {
        return line;
    }
}
