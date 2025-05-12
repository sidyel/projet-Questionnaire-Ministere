package org.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import utilitaires.ItemPourcentage;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.config.HibernateUtil;
import org.entitee.Division;
import org.entitee.Employe;
import org.entitee.EmployeId;
import org.entitee.Institution;
import org.entitee.Itemquestion;
import org.entitee.Permission;
import org.entitee.Personne;
import org.entitee.Profile;
import org.entitee.Projet;
import org.entitee.Question;
import org.entitee.Questionnaire;
import org.entitee.Reponse;
import org.entitee.Rolepermission;

public class GenericDAO<T> implements Serializable {
	private static final Log log = LogFactory.getLog(GenericDAO.class);
	private final Class<T> entityClass;
	SessionFactory sessionFactory = (SessionFactory) FacesContext.getCurrentInstance().getExternalContext()
			.getApplicationMap().get("SessionFactory");

	public GenericDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public void persist(T transientInstance) {
		log.debug("Persisting " + entityClass.getSimpleName() + " instance");
		Transaction tx = null;
		Session session = null;
		try {
			session = sessionFactory.openSession(); // Ouvrir la session
			tx = session.beginTransaction(); // Démarrer la transaction
			session.save(transientInstance); // Enregistrer l'entité
			tx.commit(); // Commit de la transaction
			log.debug("Persist successful");
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback(); // Annuler la transaction en cas d'erreur
			}
			log.error("Persist failed", e);
			throw e;
		} finally {
			if (session != null && session.isOpen()) {
				session.close(); // Fermer la session après la transaction
			}
		}
	}

	public void remove(T persistentInstance) {
		log.debug("Removing " + entityClass.getSimpleName() + " instance");
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.delete(persistentInstance);
			tx.commit();
			log.debug("Remove successful");
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			log.error("Remove failed", e);
			throw e;
		}
	}

	public void merge(T detachedInstance) {
		log.debug("Merging " + entityClass.getSimpleName() + " instance");
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.merge(detachedInstance);
			tx.commit();
			log.debug("Merge successful");
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			log.error("Merge failed", e);
			throw e;
		}
	}

	public T findById(int id) {
		log.debug("Getting " + entityClass.getSimpleName() + " instance with id: " + id);
		try (Session session = sessionFactory.openSession()) {
			T entity = session.get(entityClass, id);
			log.debug("Get successful");
			return entity;
		} catch (HibernateException e) {
			log.error("Get failed", e);
			throw e;
		}
	}

	public T findById1(EmployeId id) {
		log.debug("Getting " + entityClass.getSimpleName() + " instance with id: " + id);
		try (Session session = sessionFactory.openSession()) {
			T entity = session.get(entityClass, id);
			log.debug("Get successful");
			return entity;
		} catch (HibernateException e) {
			log.error("Get failed", e);
			throw e;
		}
	}

	public int getNextSequenceValue() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			log.debug("Opening session to fetch next sequence value");
			// Exécution de la requête SQL pour obtenir la prochaine valeur de la séquence
			String sql = "SELECT nextval('global_seq')";
			BigInteger nextValue = (BigInteger) session.createNativeQuery(sql).uniqueResult();
			log.debug("Next sequence value: " + nextValue);

			// Convertir BigInteger en Long
			return (int) nextValue.longValue();
		} catch (HibernateException e) {
			log.error("Error fetching next sequence value", e);
			throw e;
		}
	}

	public void modifierEmploye(Employe employe) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			// Réattacher l'entité Employe
			Employe mergedEmploye = (Employe) session.merge(employe);

			if (employe.getPersonne().getProfile() != null) {
				Profile mergedPersonne1 = (Profile) session.merge(employe.getPersonne().getProfile());
				mergedEmploye.getPersonne().setProfile(mergedPersonne1);
			}

			// Réattacher l'entité Personne associée et la lier à l'employé
			if (employe.getPersonne() != null) {
				Personne mergedPersonne = (Personne) session.merge(employe.getPersonne());
				mergedEmploye.setPersonne(mergedPersonne);
			}

			// Réattacher l'entité Institution associée (si présente) et la lier à l'employé
			if (employe.getInstitution() != null) {
				Institution mergedInstitution = (Institution) session.merge(employe.getInstitution());
				mergedEmploye.setInstitution(mergedInstitution);
			}

			// Enregistrer ou mettre à jour l'employé et ses associations
			session.saveOrUpdate(mergedEmploye);

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public List<T> findAll() {
		log.debug("Fetching all instances of " + entityClass.getSimpleName());

		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<T> criteriaQuery = builder.createQuery(entityClass);
			Root<T> root = criteriaQuery.from(entityClass);
			criteriaQuery.select(root);

			List<T> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Employe> getAllEmployes() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Employe> employes = session.createQuery("FROM Employe e", Employe.class).list();
		session.close();
		return employes;
	}

	public List<Employe> lesAdmins(String libelle) {
		log.debug("Fetching all questions for questionnaire with id: " + libelle);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Employe> criteriaQuery = builder.createQuery(Employe.class);
			Root<Employe> root = criteriaQuery.from(Employe.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root)
					.where(builder.equal(root.get("personne").get("profile").get("libelle"), libelle));

			List<Employe> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Division> divisionInstitue(int id) {
		log.debug("Fetching all questions for questionnaire with id: " + id);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Division> criteriaQuery = builder.createQuery(Division.class);
			Root<Division> root = criteriaQuery.from(Division.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root).where(builder.equal(root.get("institution").get("idins"), id));

			List<Division> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Question> findQuestionsByQuestionnaire(int questionnaireId) {
		log.debug("Fetching all questions for questionnaire with id: " + questionnaireId);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Question> criteriaQuery = builder.createQuery(Question.class);
			Root<Question> root = criteriaQuery.from(Question.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root)
					.where(builder.equal(root.get("questionnaire").get("idquestionnaire"), questionnaireId));

			List<Question> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Questionnaire> findQuestionnnaireByproject(int projectId) {
		log.debug("Fetching all questions for questionnaire with id: " + projectId);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Questionnaire> criteriaQuery = builder.createQuery(Questionnaire.class);
			Root<Questionnaire> root = criteriaQuery.from(Questionnaire.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root).where(builder.equal(root.get("projet").get("idprojet"), projectId));

			List<Questionnaire> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Questionnaire> findQuestionnaires(int projetId) {
		log.debug("Fetching all questions for questionnaire with id: " + projetId);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Questionnaire> criteriaQuery = builder.createQuery(Questionnaire.class);
			Root<Questionnaire> root = criteriaQuery.from(Questionnaire.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root).where(builder.equal(root.get("projet").get("idprojet"), projetId));

			List<Questionnaire> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Itemquestion> finditemsQuestionsByQuestionnaire(int questionnaireId) {
		log.debug("Fetching all questions for questionnaire with id: " + questionnaireId);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Itemquestion> criteriaQuery = builder.createQuery(Itemquestion.class);
			Root<Itemquestion> root = criteriaQuery.from(Itemquestion.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root).where(builder.equal(root.get("question").get("idquestion"), questionnaireId));

			List<Itemquestion> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Institution> findinstitution(int instituid) {
		log.debug("Fetching all questions for questionnaire with id: " + instituid);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Institution> criteriaQuery = builder.createQuery(Institution.class);
			Root<Institution> root = criteriaQuery.from(Institution.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root)
					.where(builder.equal(root.get("categorieinstitution").get("idcatins"), instituid));

			List<Institution> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Rolepermission> findRolePermission(int idPes) {
		log.debug("Fetching all questions for questionnaire with id: " + idPes);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Rolepermission> criteriaQuery = builder.createQuery(Rolepermission.class);
			Root<Rolepermission> root = criteriaQuery.from(Rolepermission.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root).where(builder.equal(root.get("personne").get("idpersonne"), idPes));

			List<Rolepermission> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public Profile findByProfile(String permissionName) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Profile> criteriaQuery = builder.createQuery(Profile.class);
			Root<Profile> root = criteriaQuery.from(Profile.class);
			criteriaQuery.select(root).where(builder.equal(root.get("libelle"), permissionName));
			return session.createQuery(criteriaQuery).uniqueResult();
		} catch (HibernateException e) {
			// Gérer l'exception selon vos besoins (log, relancer, etc.)
			throw e;
		}
	}

	public List<Projet> listeProjet(int institu) {
		log.debug("Fetching all questions for questionnaire with id: " + institu);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Projet> criteriaQuery = builder.createQuery(Projet.class);
			Root<Projet> root = criteriaQuery.from(Projet.class);
			// On accède à l'objet Questionnaire et à sa propriété d'identifiant (ici
			// supposée "idquestionnaire")
			criteriaQuery.select(root).where(builder.equal(root.get("institution").get("idins"), institu));

			List<Projet> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public List<Rolepermission> listePermission(int id) {
		log.debug("Fetching all questions for questionnaire with id: " + id);
		try (Session session = sessionFactory.openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Rolepermission> criteriaQuery = builder.createQuery(Rolepermission.class);
			Root<Rolepermission> root = criteriaQuery.from(Rolepermission.class);

			criteriaQuery.select(root).where(builder.equal(root.get("personne").get("idpersonne"), id));

			List<Rolepermission> results = session.createQuery(criteriaQuery).getResultList();
			log.debug("Fetch successful, total records: " + results.size());
			return results;
		} catch (HibernateException e) {
			log.error("Fetch failed", e);
			throw e;
		}
	}

	public boolean utilisateurARepondu(int idQuestionnaire, int idUtilisateur) {
		// Vérifier si l'utilisateur a des réponses enregistrées pour ce questionnaire
		Session session = sessionFactory.openSession();
		Long count = (Long) session.createQuery(
				"SELECT COUNT(r) FROM Reponse r WHERE r.employe.id.idemploye = :idUtilisateur AND r.question.questionnaire.idquestionnaire = :idQuestionnaire",
				Long.class).setParameter("idUtilisateur", idUtilisateur)
				.setParameter("idQuestionnaire", idQuestionnaire).getSingleResult();

		return count != null && count > 0;
	}

	public List<Reponse> trouverReponsesPourQuestionnaireEtUser(int idQuestionnaire, int idUtilisateur) {
		Session session = sessionFactory.openSession();

		List<Reponse> reponses = session.createQuery(
				"SELECT r FROM Reponse r WHERE r.employe.id.idemploye = :idUtilisateur AND r.question.questionnaire.idquestionnaire = :idQuestionnaire",
				Reponse.class).setParameter("idUtilisateur", idUtilisateur)
				.setParameter("idQuestionnaire", idQuestionnaire).getResultList();

		return reponses;
	}

	public List<Reponse> trouverReponsesDistinctesParPersonnePourQuestionnaire(int idQuestionnaire) {
		Session session = sessionFactory.openSession();

		List<Reponse> reponses = session
				.createQuery("SELECT r FROM Reponse r " + "WHERE r.idreponse IN ("
						+ "SELECT MIN(r2.idreponse) FROM Reponse r2 "
						+ "WHERE r2.question.questionnaire.idquestionnaire = :idQuestionnaire "
						+ "GROUP BY r2.employe.personne.idpersonne" + ")", Reponse.class)
				.setParameter("idQuestionnaire", idQuestionnaire).getResultList();

		return reponses;
	}

	public Personne connexion(String login) {

		try {

			Session ss = sessionFactory.openSession();
			Transaction tx = ss.beginTransaction();
			Query q = ss.createQuery("SELECT p FROM Personne p WHERE p.login = :login ", Personne.class);

			q.setParameter("login", login);
			return (Personne) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Gérez le cas où aucune personne n'est trouvée
	}
	
	public Employe recupererEmployebis(int id) {

		try {

			Session ss = sessionFactory.openSession();
			Transaction tx = ss.beginTransaction();
			Query q = ss.createQuery("SELECT p FROM Employe p WHERE p.personne.idpersonne = :id ", Employe.class);

			q.setParameter("id", id);
			return (Employe) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Gérez le cas où aucune personne n'est trouvée
	}

	public Employe recupererEmploye(String login) {
		Employe employe = null;
		try (Session ss = sessionFactory.openSession()) { // try-with-resources pour fermer la session
			Query q = ss.createQuery(
					"SELECT p FROM Employe p WHERE p.personne.login = :login OR p.personne.email1 = :login",
					Employe.class);
			q.setParameter("login", login);

			employe = (Employe) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Aucun employé trouvé avec le login : " + login);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employe;
	}

	public void supprimerProjetEtQuestionnaires(int idProjet) {
		Session session = sessionFactory.openSession();

		Transaction tx = null;
		try {
			// Démarrage de la transaction
			tx = session.beginTransaction();

			// Obtention du CriteriaBuilder
			CriteriaBuilder cb = session.getCriteriaBuilder();

			// Suppression des questionnaires associés au projet
			CriteriaDelete<Questionnaire> deleteQuestionnaire = cb.createCriteriaDelete(Questionnaire.class);
			Root<Questionnaire> questionnaireRoot = deleteQuestionnaire.from(Questionnaire.class);
			deleteQuestionnaire.where(cb.equal(questionnaireRoot.get("projet").get("idprojet"), idProjet));
			int nbQuestionnairesSupprimes = session.createQuery(deleteQuestionnaire).executeUpdate();
			System.out.println("Nombre de questionnaires supprimés : " + nbQuestionnairesSupprimes);

			// Suppression du projet
			CriteriaDelete<Projet> deleteProjet = cb.createCriteriaDelete(Projet.class);
			Root<Projet> projetRoot = deleteProjet.from(Projet.class);
			deleteProjet.where(cb.equal(projetRoot.get("idprojet"), idProjet));
			int nbProjetsSupprimes = session.createQuery(deleteProjet).executeUpdate();
			System.out.println("Nombre de projets supprimés : " + nbProjetsSupprimes);

			// Valider la transaction
			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback(); // Annuler la transaction en cas d'erreur
			}
			e.printStackTrace();
		}
	}

	// Pour verifier si le user a reponse a la question idQuestion
	public Reponse findReponseQuestion(int idQuestion, int idPersonne) {
		Session s = sessionFactory.openSession();
		try {
			return s.createQuery(
					"from Reponse r where r.question.idquestion = :idQuestion and r.employe.personne.idpersonne = :idPersonne",
					Reponse.class).setParameter("idQuestion", idQuestion).setParameter("idPersonne", idPersonne)
					.uniqueResult();
		} finally {
			s.close();
		}
	}

	public List<ItemPourcentage> getItemPourcentages(Question question, Long totalEligible) {
		Session ss = sessionFactory.openSession();
		Transaction tx = ss.beginTransaction();

		// Calcul pour chaque item dont la réponse n'est pas vide,
		// on renvoie le pourcentage et le nombre de réponses (count(r))
		String hqlItems = "select new utilitaires.ItemPourcentage(i.valeur, "
				+ " ((count(r)*1.0)*100.0 / :totalEligible), count(r)) " + "from Itemquestion i "
				+ "left join Reponse r with r.question = :question and r.valeur = i.valeur "
				+ "where i.question = :question " + "group by i.valeur";
		org.hibernate.query.Query<ItemPourcentage> queryItems = ss.createQuery(hqlItems, ItemPourcentage.class);
		queryItems.setParameter("question", question);
		queryItems.setParameter("totalEligible", (double) totalEligible);
		List<ItemPourcentage> itemResults = queryItems.getResultList();

		// Calcul du nombre de réponses "vides" (null ou chaîne vide)
		String hqlNull = "select count(r2) from Reponse r2 " + "where r2.question = :question "
				+ "and (r2.valeur is null or r2.valeur = '') " + "and r2.idreponse in ( "
				+ "  select MIN(r3.idreponse) from Reponse r3 " + "  where r3.question = :question "
				+ "  group by r3.employe.personne.idpersonne" + ")";
		Long nullCount = (Long) ss.createQuery(hqlNull).setParameter("question", question).uniqueResult();

		double nullPercentage = 0.0;
		if (totalEligible > 0) {
			nullPercentage = (nullCount * 100.0) / totalEligible;
		}

		// Ajout du résultat pour "Aucune réponse" en incluant le nombre de personnes
		// ayant laissé une réponse vide
		itemResults.add(new utilitaires.ItemPourcentage("Aucune réponse", nullPercentage, nullCount));

		tx.commit();
		ss.close();
		return itemResults;
	}

	public Long nombreDePersonneAyantReponduAuQuestonnaire(int idQuestionnaire) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			// Cette requête compte distinctement les identifiants de personnes associées
			// aux réponses valides
			String hql = "select count(distinct r.employe.personne.idpersonne) "
					+ "from Reponse r where r.question.questionnaire.idquestionnaire=:id";
			Query query = session.createQuery(hql, Long.class);
			query.setParameter("id", idQuestionnaire);
			Long result = (Long) query.getSingleResult();
			tx.commit();
			return result;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		} finally {
			session.close();
		}
	}

	public Institution institutionCode(int code) {

		try {

			Session ss = sessionFactory.openSession();
			Transaction tx = ss.beginTransaction();
			Query q = ss.createQuery("SELECT p FROM Institution p WHERE p.code = :login ", Institution.class);

			q.setParameter("login", code);
			return (Institution) q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Gérez le cas où aucune personne n'est trouvée
	}

	public List<Employe> getEmployesSansReponsePourQuestionnaire(int idQuestionnaire,int institution) {
		List<Employe> employes = null;

		try (Session session = sessionFactory.openSession()) {
			String hql = "SELECT e FROM Employe e " + "WHERE e.institution.idins= :idinst AND e.id.idemploye NOT IN ( "
					+ "    SELECT DISTINCT r.employe.id.idemploye " + "    FROM Reponse r " + "    JOIN r.question q "
					+ "    WHERE q.questionnaire.idquestionnaire = :idQ " + ")";

			employes = session.createQuery(hql, Employe.class).setParameter("idinst", institution).setParameter("idQ", idQuestionnaire).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return employes;
	}
	
	public Institution getInstitutionByQuestionnaireId(int idQuestionnaire) {
	    Institution institution = null;
	    try (Session session = sessionFactory.openSession()) {
	        String hql = "SELECT q.projet.institution FROM Questionnaire q " +
	                     "WHERE q.idquestionnaire = :idQ";
	        institution = session.createQuery(hql, Institution.class)
	                             .setParameter("idQ", idQuestionnaire)
	                             .uniqueResult();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return institution;
	}


}
