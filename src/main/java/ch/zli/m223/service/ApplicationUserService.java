package ch.zli.m223.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import ch.zli.m223.domain.entity.ApplicationUser;

@ApplicationScoped
public class ApplicationUserService {
    @Inject
    private EntityManager entityManager;

    @Transactional
    public ApplicationUser createApplicationUser(ApplicationUser user) {
        entityManager.persist(user);
        return user;
    }

    @Transactional
    public void deleteApplicationUser(Long id) {
        entityManager.remove(entityManager.find(ApplicationUser.class, id));
    }

    public List<ApplicationUser> findAll() {
        var query = entityManager.createQuery("FROM ApplicationUser", ApplicationUser.class);
        return query.getResultList();
    }

    public ApplicationUser find(long id) {
        return entityManager.find(ApplicationUser.class, id);
    }

    @Transactional
    public ApplicationUser update(ApplicationUser user) {
        return entityManager.merge(user);
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
        return entityManager.createQuery("COUNT * FROM ApplicationUser", Long.class).getSingleResult();
    }
}