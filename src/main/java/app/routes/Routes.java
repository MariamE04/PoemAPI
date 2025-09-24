package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private PoemRoutes poemRoutes = new PoemRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            // root endpoint
            get("/", ctx -> ctx.result("Welcome to Poem API!"));

            // poem endpoints
            path("/poems", poemRoutes.getRoutes());

        };
    }
}
