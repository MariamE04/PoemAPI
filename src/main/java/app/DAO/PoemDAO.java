package app.DAO;

import app.dtos.PoemDTO;
import app.entities.Poem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class PoemDAO {

    private final EntityManagerFactory emf;

    public PoemDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Poem create(Poem poem){
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.persist(poem);

            em.getTransaction().commit();
        }
        return poem;
    }

    public Poem getById(int id){
        try(EntityManager em = emf.createEntityManager()){
            return em.find(Poem.class, id);
        }
    }


    public List<Poem> getAll(){
        try(EntityManager em = emf.createEntityManager()){

            List<Poem> poems = em.createQuery("SELECT p FROM Poem p", Poem.class)
                    .getResultList();

            return poems;
        }
    }

    public Poem update(int id, Poem poem){
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.merge(poem);

            em.getTransaction().commit();

            return poem;
        }
    }

    public boolean delete(int id){
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            Poem delete = em.find(Poem.class,id);
            if (delete != null) {
                em.remove(delete);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;

            }
        }
    }



}
