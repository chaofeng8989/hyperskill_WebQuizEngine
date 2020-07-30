package engine.model;

import lombok.Data;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class ResponseOBJ {
    public ResponseOBJ(Collection<Object> content) {
        this.content = content;
    }

    Collection<Object> content = new ArrayList<>();

}
