package ch.ydavid.pizzabot.DAO;

import ch.ydavid.pizzabot.entity.GuildConfig;

import javax.persistence.*;

public class GuildConfigDAO {

    private final EntityManagerFactory entityManagerFactory;

    public GuildConfigDAO() {
        entityManagerFactory = Persistence.createEntityManagerFactory("ch.ydavid.pizzabot");
    }

    public boolean checkIfConfigExists(String guildID) {
        boolean configExists = false;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            TypedQuery<Long> query = entityManager.createQuery("select count(*) from GuildConfig g where g.guildId = :guildID", Long.class);
            query.setParameter("guildID", guildID);
            configExists = query.getSingleResult() == 1;
            System.out.println(query.getSingleResult());
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return configExists;

    }

    public boolean peristConfig(GuildConfig config) {

        boolean success = true;

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            entityManager.persist(config);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return success;

    }

    public boolean mergeConfig(GuildConfig config) {

        boolean success = true;

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            entityManager.merge(config);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return success;

    }

    public GuildConfig getEntry(String guildID) {

        GuildConfig gc = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            TypedQuery<GuildConfig> query = entityManager.createQuery("SELECT gc FROM GuildConfig gc where gc.guildId = :guildID", GuildConfig.class);
            query.setParameter("guildID", guildID);

            gc = query.getSingleResult();

            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return gc;

    }
}
