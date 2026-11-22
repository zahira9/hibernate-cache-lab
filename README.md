# Hibernate Cache Lab

TP 7 sur l'optimisation JPA/Hibernate : cache de second niveau, `JOIN FETCH` et Entity Graph.

## Technologies

- Java 11
- Maven
- JPA 2.2
- Hibernate 5.6
- Ehcache 2 via Hibernate Ehcache
- H2
- JUnit 5

## Exécuter

```bash
mvn test
mvn exec:java -Dexec.mainClass=com.example.App
```

## Contenu

- Entités `Auteur` et `Livre`
- Relation bidirectionnelle `OneToMany` / `ManyToOne`
- Cache de second niveau avec Ehcache
- Chargement avec `JOIN FETCH`
- Chargement avec Named Entity Graph
- Mesure des performances
