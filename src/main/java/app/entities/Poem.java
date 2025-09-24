package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder

@Entity
public class Poem  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="style", nullable = false)
    private String style;

    @Column(name="text", nullable = false)
    private String text;

    @Column(name="author", nullable = false)
    private String author;

}
