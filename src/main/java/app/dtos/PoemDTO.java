package app.dtos;

import app.entities.Poem;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PoemDTO {
        private int id;
        private String style;
        private String text;
        private String author;

        public PoemDTO(Poem poem) {
            this.id = poem.getId();
            this.style = poem.getStyle();
            this.text = poem.getText();
            this.author = poem.getAuthor();
        }

}
