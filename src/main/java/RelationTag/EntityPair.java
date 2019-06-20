package RelationTag;

import java.util.ArrayList;
import java.util.List;

public class EntityPair {
    public String getE1() {
        return e1;
    }

    public void setE1(String e1) {
        this.e1 = e1;
    }
    public EntityPair(String e1,String e2)
    {
        this.e1 = e1;
        this.e2 = e2;
        this.originalData = new ArrayList<String>();
        this.relations = new ArrayList<String>();
    }
    public EntityPair()
    {
        this.relations = new ArrayList<String>();
    }
    public String getE2() {
        return e2;
    }

    public void setE2(String e2) {
        this.e2 = e2;
    }

    private String e1;
    private String e2;

    public List<String> getOriginalData() {
        return originalData;
    }

    public void setOriginalData(List<String> originalData) {
        this.originalData = originalData;
    }

    public List<String> getRelations() {
        return relations;
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
    }

    public List<String> originalData;
    public List<String> relations;
    public EntityPair(String e1,String e2,List<String> relations,List<String> originalData)
    {
        this.e1 = e1;
        this.e2 = e2;
        this.relations = relations;
        this.originalData = originalData;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if(obj == null||getClass()!=obj.getClass())
        {
            return false;
        }
        EntityPair p = (EntityPair)obj;
        return (this.e1.equals(p.e1) && this.e2.equals(p.e2));
    }

}
