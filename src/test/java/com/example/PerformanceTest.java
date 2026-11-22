package com.example;

import com.example.model.Auteur;
import com.example.service.DataInitService;
import com.example.service.PerformanceTestService;
import org.junit.jupiter.api.*;
import javax.persistence.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PerformanceTest {
    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void createFactory() {
        emf = Persistence.createEntityManagerFactory("hibernate-cache-pu");
    }

    @BeforeEach
    void createManager() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Livre").executeUpdate();
        em.createQuery("DELETE FROM Auteur").executeUpdate();
        em.getTransaction().commit();
        new DataInitService(em).initData();
    }

    @AfterEach
    void closeManager() {
        em.close();
    }

    @AfterAll
    static void closeFactory() {
        emf.close();
    }

    @Test
    void joinFetchLoadsAuthorsAndBooksInOneGraph() {
        List<Auteur> auteurs = em.createQuery(
                "SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class)
                .getResultList();
        assertEquals(10, auteurs.size());
        assertTrue(auteurs.stream().allMatch(a -> a.getLivres().size() == 5));
    }

    @Test
    void entityGraphLoadsBooksDeclaratively() {
        EntityGraph<Auteur> graph = em.createEntityGraph(Auteur.class);
        graph.addAttributeNodes("livres");
        TypedQuery<Auteur> query =
                em.createQuery("SELECT DISTINCT a FROM Auteur a", Auteur.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        List<Auteur> auteurs = query.getResultList();
        assertEquals(10, auteurs.size());
        assertTrue(auteurs.stream().allMatch(a -> a.getLivres().size() == 5));
    }

    @Test
    void performanceMethodsReturnNonNegativeDurations() {
        PerformanceTestService service = new PerformanceTestService(em);
        assertTrue(service.testPerformanceSansCache() >= 0);
        assertTrue(service.testPerformanceAvecJoinFetch() >= 0);
        assertTrue(service.testPerformanceAvecEntityGraph() >= 0);
        assertTrue(service.testPerformanceAvecCache() >= 0);
    }
}
