package com.example;

import com.example.service.DataInitService;
import com.example.service.PerformanceTestService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hibernate-cache-pu");
        EntityManager em = emf.createEntityManager();
        try {
            new DataInitService(em).initData();
            PerformanceTestService performance = new PerformanceTestService(em);

            System.out.println("Durée sans optimisation : " +
                    performance.testPerformanceSansCache() + " ms");
            System.out.println("Durée avec JOIN FETCH : " +
                    performance.testPerformanceAvecJoinFetch() + " ms");
            System.out.println("Durée avec Entity Graph : " +
                    performance.testPerformanceAvecEntityGraph() + " ms");
            System.out.println("Durée avec cache (lecture 2) : " +
                    performance.testPerformanceAvecCache() + " ms");
        } finally {
            em.close();
            emf.close();
        }
    }
}
