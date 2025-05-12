package managedBean;

import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.faces.view.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import org.dao.GenericDAO;
import org.entitee.Projet;
import org.entitee.Questionnaire;
import org.entitee.Rolepermission;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.entitee.Employe;
import org.entitee.Institution;
import org.entitee.Personne;

@Named("homeView")
@ViewScoped
public class HomeView implements Serializable {

	FacesContext facesContext = FacesContext.getCurrentInstance();
	ExternalContext externalContext = facesContext.getExternalContext();
	HttpSession session = (HttpSession) externalContext.getSession(true);

	private List<Projet> projects = new ArrayList<>();
	private Employe currentEmploye;

	private List<Questionnaire> questionnaire = new ArrayList<>();

	private Institution institution = new Institution();

	private GenericDAO<Projet> daoProjet = new GenericDAO<>(Projet.class);
	private GenericDAO<Questionnaire> daoQuest = new GenericDAO<>(Questionnaire.class);

	private List<Rolepermission> rolepermissions = new ArrayList<Rolepermission>();

	Employe employe;

	@PostConstruct
	public void init() {
		// Récupérer l'employé connecté depuis la session
		currentEmploye = (Employe) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.get("employe");

		if (employe == null) {
			employe = new Employe();
			employe.setPersonne(new Personne());
		}

		if (currentEmploye != null) {
			Institution institution = currentEmploye.getInstitution();

			rolepermissions = daoProjet.listePermission(currentEmploye.getPersonne().getIdpersonne());
			if (institution != null) { // Chargez la liste des projets pour l'institution
				projects = daoProjet.listeProjet(currentEmploye.getInstitution().getIdins());

			}
		}

	}

	public void loadQuestionnaires(int projectId) {
		questionnaire = daoProjet.findQuestionnaires(projectId);
	}

	public String goToQuestions(int questionnaireId) {
		return "repondreQuestion.xhtml?faces-redirect=true&id=" + questionnaireId;
	}

	public boolean utilisateurARepondu(int idQuestionnaire) {
		// Vérifier si l'utilisateur a répondu au questionnaire
		return daoProjet.utilisateurARepondu(idQuestionnaire, currentEmploye.getId().getIdemploye());
	}

	public String afficherQuestionsOuReponses(Questionnaire idQuestionnaire) {
		
		Questionnaire q=daoQuest.findById(idQuestionnaire.getIdquestionnaire());
		if (q.getEtat()!=null && q.getEtat().equals("Clos")) {
			return "mesReponses.xhtml?faces-redirect=true&id=" + idQuestionnaire.getIdquestionnaire();
		} else {
			return "repondreQuestion.xhtml?faces-redirect=true&id=" + idQuestionnaire.getIdquestionnaire();
		}
	}

	public boolean hasPermission(String permissionName) {
		if (currentEmploye != null && rolepermissions != null) {
			for (Rolepermission rp : rolepermissions) {
				if (rp.getPermission() != null && rp.getPermission().getPermission() != null
						&& rp.getPermission().getPermission().equalsIgnoreCase(permissionName)) {
					return true;
				}
			}
		}
		return false;
	}

	public String connexion() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// On récupère la personne via le login
		Personne foundPersonne = daoProjet.connexion(employe.getPersonne().getLogin());
		Employe foundEmploye=daoProjet.recupererEmployebis(foundPersonne.getIdpersonne());
		System.out.println(foundEmploye.getPersonne().getLogin());

		if (foundPersonne == null) 
		{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Login incorrect.");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return null;
		}

		// Vérification du mot de passe
		if (!passwordEncoder.matches(employe.getPersonne().getMotpasse(), foundPersonne.getMotpasse())) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Mot de passe incorrect.");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return null;
		}

		try {

			if ("Employe".equalsIgnoreCase(foundPersonne.getProfile().getLibelle())) {

				session.setAttribute("employe", foundEmploye);
				projects = daoProjet.listeProjet(foundEmploye.getInstitution().getIdins());
				System.out.println(projects.size());
				FacesContext.getCurrentInstance().getExternalContext().redirect("acceuil.xhtml");
			} else if ("Admin".equalsIgnoreCase(foundPersonne.getProfile().getLibelle())) {

				session.setAttribute("admin", foundEmploye);

				FacesContext.getCurrentInstance().getExternalContext().redirect("adminPage.xhtml");
			} else {
				// Rôle inconnu
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Rôle inconnu.");
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Getters et setters
	public List<Projet> getProjects() {
		return projects;
	}

	public void setProjects(List<Projet> projects) {
		this.projects = projects;
	}

	public Employe getCurrentEmploye() {
		return currentEmploye;
	}

	public void setCurrentEmploye(Employe currentEmploye) {
		this.currentEmploye = currentEmploye;
	}

	public Employe getEmploye() {
		return employe;
	}

	public void setEmploye(Employe employe) {
		this.employe = employe;
	}

	public List<Questionnaire> getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(List<Questionnaire> questionnaire) {
		this.questionnaire = questionnaire;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

}
