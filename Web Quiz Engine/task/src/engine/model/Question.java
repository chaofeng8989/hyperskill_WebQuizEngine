package engine.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    int id;

    @NotBlank (message = "title is mandatory")
    String title;

    @NotBlank (message = "text is mandatory")
    String text;

    @NotNull
    @NotEmpty
    @Size(min = 2)
    @ElementCollection
    List<String> options;

    @ElementCollection
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    Set<Integer> answer;

    public  static Question getCopy(Question q) {
        Question newQ = new Question();
        newQ.setId(q.getId());
        newQ.setOptions(q.getOptions());
        newQ.setText(q.getText());
        newQ.setTitle(q.getTitle());
        return newQ;
    }
}
