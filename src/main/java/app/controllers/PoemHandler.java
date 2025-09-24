package app.controllers;

import app.DAO.PoemDAO;
import app.config.HibernateConfig;
import app.dtos.PoemDTO;
import app.entities.Poem;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PoemHandler {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    static PoemDAO dao = new PoemDAO(emf);

    private static final Logger logger = LoggerFactory.getLogger(PoemHandler.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    public void createPoem(Context ctx) {
        PoemDTO dto = ctx.bodyAsClass(PoemDTO.class);
        Poem poem = new Poem();
        poem.setStyle(dto.getStyle());
        poem.setText(dto.getText());
        poem.setAuthor(dto.getAuthor());

        Poem saved = dao.create(poem);
        ctx.status(HttpStatus.CREATED);
        ctx.json(new PoemDTO(saved));
    }

    public void getAllPoems(Context ctx) {
        List<Poem> poems = dao.getAll();
        List<PoemDTO> dtos = poems.stream().map(PoemDTO::new).toList();
        ctx.status(HttpStatus.OK);
        ctx.json(dtos);
    }

    public void getPoemById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Poem poem = dao.getById(id);
        if (poem != null) {
            ctx.status(HttpStatus.OK);
            ctx.json(new PoemDTO(poem));
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Poem not found");
        }
    }

    public void updatePoem(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        PoemDTO dto = ctx.bodyAsClass(PoemDTO.class);

        Poem poemToUpdate = new Poem();
        poemToUpdate.setId(id); // vigtigt!
        poemToUpdate.setStyle(dto.getStyle());
        poemToUpdate.setText(dto.getText());
        poemToUpdate.setAuthor(dto.getAuthor());

        Poem updated = dao.update(id, poemToUpdate);
        ctx.status(HttpStatus.OK);
        ctx.json(new PoemDTO(updated));
    }

    public void deletePoem(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = dao.delete(id);

        if (deleted) {
            ctx.result("Poem with id " + id + " deleted");
            ctx.status(HttpStatus.OK);
        } else {
            ctx.result("Poem not found");
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

}
