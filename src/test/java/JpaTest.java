import ch.ydavid.pizzabot.entity.Person;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaTest {

    EntityManagerFactory entityManagerFactory;

    @Test
    public void testJPA() {
        entityManagerFactory = Persistence.createEntityManagerFactory("ch.ydavid.pizzabot");

        Person person = new Person();

        person.setName("David");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.persist(person);

    }
}
