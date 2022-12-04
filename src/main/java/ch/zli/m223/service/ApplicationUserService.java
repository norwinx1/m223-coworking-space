package ch.zli.m223.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import ch.zli.m223.domain.entity.ApplicationUser;
import ch.zli.m223.domain.exception.ConflictException;

@ApplicationScoped
public class ApplicationUserService {
    @Inject
    private EntityManager entityManager;

    @Transactional
    public ApplicationUser createApplicationUser(ApplicationUser user) throws ConflictException {
        try {
            entityManager.persist(user);
            return user;
        } catch (PersistenceException e) {
            throw new ConflictException("Email already in use");
        }
    }

    @Transactional
    public void deleteApplicationUser(Long id) {
        entityManager
                .createQuery("DELETE FROM ApplicationUser u WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<ApplicationUser> findAll() {
        var query = entityManager.createQuery("FROM ApplicationUser", ApplicationUser.class);
        return query.getResultList();
    }

    public ApplicationUser find(long id) {
        return entityManager.find(ApplicationUser.class, id);
    }

    @Transactional
    public ApplicationUser update(ApplicationUser user) throws ConflictException {
        try {
            return entityManager.merge(user);
        } catch (PersistenceException e) {
            throw new ConflictException("Email already in use");
        }
    }

    public Optional<ApplicationUser> findByEmail(String email) {
        return entityManager
                .createQuery("SELECT u FROM ApplicationUser u WHERE u.email = :email", ApplicationUser.class)
                .setParameter("email",
                        email)
                .getResultStream()
                .findFirst();
    }

    public Long count() {
        return entityManager.createQuery("SELECT COUNT(id) FROM ApplicationUser u", Long.class).getSingleResult();
    }
}
