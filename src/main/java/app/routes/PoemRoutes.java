package app.routes;

import app.controllers.PoemHandler;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class PoemRoutes {
   // HighscoreController highscoreController = new HighscoreController();

   public EndpointGroup getRoutes() {
       PoemHandler handler = new PoemHandler(); // instans

       return () -> {
           get(handler::getAllPoems);
           post(handler::createPoems);
           path("/{id}", () -> {
               get(handler::getPoemById);
               put(handler::updatePoem);
               delete(handler::deletePoem);
           });
       };
    }
}