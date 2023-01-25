package moscow.ptnl.app.ecppis.model;

import java.util.List;

public class EntityData {
    private List<Attribute> attributes;

    public EntityData() {
    }

    public EntityData(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
