import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.entities.Poem;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PoemsApiTest {

    private Javalin app;
    private EntityManagerFactory emf;

    @BeforeAll
    void setup() {
     // Start test db
        emf = HibernateConfig.getEntityManagerFactoryForTest();

     // Start javalin server
        app = ApplicationConfig.startServer(7070);

     // Konfigurer RestAssured base URI
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
        RestAssured.basePath = "/api/poem";

    }

    @AfterAll
    void tearDown() {
        // Stop server
        ApplicationConfig.stopServer(app);

        // Luk emf
        if(emf != null){
            emf.close();
        }

    }

    @BeforeEach
    void setUpTestData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.createQuery("DELETE FROM Poem").executeUpdate(); // ryd databasen

            Poem p1 = Poem.builder()
                    .style("Haiku")
                    .text("An old silent pond...")
                    .author("Basho")
                    .build();
            em.persist(p1);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    //GET poem by id (get)
    @Test
    void testGetPoemById() {
        // Perform GET request and validate response using Hamcrest matchers
        given()
                .when()
                .get("/poems/1")
                .then()
                .statusCode(200)
                .body("style", is("Haiku"))
                .body("text", containsString("silent pond"))
                .body("author", is("Basho"));
    }


    // CREATE a new poem (post)
    @Test
    void testPostPoem() {
        String newPoemJson = """
                    {
                        "style": "Sonnet",
                        "text": "Shall I compare thee to a summer's day?",
                        "author": "Shakespeare"
                     }
                """;

        given()
                .contentType("application/json")
                .body(newPoemJson)
                .when()
                .post("/poems")
                .then()
                .statusCode(201)
                .body("style", is("Sonnet"))
                .body("author", is("Shakespeare"));

    }

    //UPDATE poem by id (put)
    @Test
    void testPutPoem() {
        String updatedPoemJson = """
               {
                "style": "Haiku",
                "text": "Autumn moonlight— a worm digs silently into the chestnut.",
                "author": "Basho"
               } 
         """;


        given()
                .contentType("application/json")
                .body(updatedPoemJson)
                .when()
                .put("poems/1")
                .then()
                .statusCode(200)
                .body("text", containsString("Autumn moonlight"));

    }

    //DELETE poem by id
    @Test
    void testDeletePoem(){
        given()
                .when()
                .delete("/poems/1")
                .then()
                .statusCode(204);

        // check it is deleted
        given()
                .when()
                .get("/poems/1")
                .then()
                .statusCode(404);
    }

    //GET all poems
    @Test
    void testGetAllPoems(){
        given()
                .when()
                .get("/poems")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    //POST "/poems" (flere på én gang)
    @Test
    void testPostMultiplePoems(){
        String poemsJson = """
        [
          {"style": "Haiku", "text": "Over the wintry forest...", "author": "Soseki"},
          {"style": "Limerick", "text": "There once was a coder named Mariam...", "author": "AI"}
        ]
    """;

        given()
                .contentType("application/json")
                .body(poemsJson)
                .when()
                .post("/poems")
                .then()
                .statusCode(201)
                .body("size()", is(2));

    }
}
