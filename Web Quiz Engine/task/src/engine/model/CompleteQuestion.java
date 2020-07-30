package engine.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
public class CompleteQuestion {
    Timestamp completedAt;

    @JsonIgnore
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    int primaryId;

    @JsonIgnore
    int userid;

    int id;
}
