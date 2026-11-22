package com.example.service;

import com.example.model.Auteur;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PerformanceTestService {
    private final EntityManager em;

    public PerformanceTestService(EntityManager em) { this.em = em; }

    public long testPerformanceSansCache() {
        long start = System.nanoTime();
        List<Auteur> auteurs = em.createQuery("SELECT DISTINCT a FROM Auteur a", Auteur.class)
                .getResultList();
        auteurs.forEach(auteur -> auteur.getLivres().size());
        return (System.nanoTime() - start) / 1_000_000;
    }

    public long testPerformanceAvecJoinFetch() {
        long start = System.nanoTime();
        List<Auteur> auteurs = em.createQuery(
                "SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class)
                .getResultList();
        auteurs.forEach(auteur -> auteur.getLivres().size());
        return (System.nanoTime() - start) / 1_000_000;
    }

    public long testPerformanceAvecEntityGraph() {
        EntityGraph<Auteur> graph = em.createEntityGraph(Auteur.class);
        graph.addAttributeNodes("livres");
        long start = System.nanoTime();
        TypedQuery<Auteur> query =
                em.createQuery("SELECT DISTINCT a FROM Auteur a", Auteur.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        List<Auteur> auteurs = query.getResultList();
        auteurs.forEach(auteur -> auteur.getLivres().size());
        return (System.nanoTime() - start) / 1_000_000;
    }

    public long testPerformanceAvecCache() {
        testPerformanceAvecJoinFetch();
        long start = System.nanoTime();
        List<Auteur> auteurs = em.createQuery(
                "SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class)
                .getResultList();
        auteurs.forEach(auteur -> auteur.getLivres().size());
        return (System.nanoTime() - start) / 1_000_000;
    }
}
