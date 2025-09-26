package app.controllers;

import app.DAO.PoemDAO;
import app.config.HibernateConfig;
import app.dtos.PoemDTO;
import app.entities.Poem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;


public class PoemHandler {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    static PoemDAO dao = new PoemDAO(emf);

    private static final Logger logger = LoggerFactory.getLogger(PoemHandler.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

   /* public void createPoem(Context ctx) {
        try {
            String body = ctx.body();
            logger.info("POST /poems body: {}", body);

            PoemDTO dto = ctx.bodyAsClass(PoemDTO.class);
            if (dto.getStyle() == null || dto.getText() == null || dto.getAuthor() == null) {
                ctx.status(400).result("Missing required fields");
                return;
            }

            Poem poem = Poem.builder()
                    .style(dto.getStyle())
                    .text(dto.getText())
                    .author(dto.getAuthor())
                    .build();

            Poem saved = dao.create(poem);
            ctx.status(HttpStatus.CREATED).json(new PoemDTO(saved));
        } catch (Exception e) {
            logger.error("Failed to create poem", e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    } */



    public void createPoems(Context ctx) {
        try {
            String body = ctx.body().trim();
            ObjectMapper mapper = new ObjectMapper();

            List<PoemDTO> dtos;

            if (body.startsWith("[")) {
                dtos = mapper.readValue(body, new TypeReference<List<PoemDTO>>() {});
            } else {
                dtos = List.of(mapper.readValue(body, PoemDTO.class));
            }

            List<PoemDTO> saved = new ArrayList<>();
            for (PoemDTO dto : dtos) {
                Poem poem = Poem.builder()
                        .style(dto.getStyle())
                        .text(dto.getText())
                        .author(dto.getAuthor())
                        .build();
                saved.add(new PoemDTO(dao.create(poem)));
            }

            // Hvis kun ét poem blev sendt, returner det som objekt i stedet for liste
            if (saved.size() == 1) {
                ctx.status(201).json(saved.get(0));
            } else {
                ctx.status(201).json(saved);
            }

        } catch (Exception e) {
            ctx.status(500).result(e.getMessage());
        }
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
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.result("Poem not found");
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

}
