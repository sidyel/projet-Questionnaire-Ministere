package managedBean;

import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.dao.GenericDAO;
import org.entitee.Division;
import org.entitee.Employe;
import org.entitee.EmployeId;
import org.entitee.Institution;
import org.entitee.Personne;
import org.entitee.Profile;
import org.primefaces.PrimeFaces;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Named
@ViewScoped
public class EmployeView implements Serializable {
	FacesContext facesContext = FacesContext.getCurrentInstance();
	ExternalContext externalContext = facesContext.getExternalContext();
	HttpSession session = (HttpSession) externalContext.getSession(true);
	
	private List<Employe> employes;
	private Employe selectedEmploye;
	private List<Employe> selectedEmployes;
	private List<Employe> admin;


	private List<Division> divisions = new ArrayList<>();

	// Listes pour les menus déroulants
	private List<Institution> institutions = new ArrayList<>();
	private List<Profile> profils = new ArrayList<>();

	// Valeurs sélectionnées depuis les menus (institution, profil, division)
	private int institution;
	private int idProfil;
	private int idDivision;

	private int numero;
	private boolean numeroValide = false;

	// DAOs
	private GenericDAO<Employe> daoEmp = new GenericDAO<>(Employe.class);
	private GenericDAO<Personne> daoPer = new GenericDAO<>(Personne.class);
	private GenericDAO<Profile> daoProfile = new GenericDAO<>(Profile.class);
	private GenericDAO<Institution> daoInstitution = new GenericDAO<>(Institution.class);
	private GenericDAO<Division> daoDivision = new GenericDAO<>(Division.class);

	// Pour la clé composite
	private EmployeId employeId = new EmployeId();

	@PostConstruct
	public void init() {
		institutions = daoInstitution.findAll();
		System.out.println(institutions.size());
		profils = daoProfile.findAll();
		employes = daoEmp.findAll();
		divisions=daoDivision.findAll();
		admin = daoEmp.lesAdmins("admin");
		if (selectedEmploye == null) {
			selectedEmploye = new Employe();
			selectedEmploye.setPersonne(new Personne());
		}
	}

	public void verifierNumero() {
		for (Institution institution : institutions) {

			if (institution.getCode()==numero) {
				session.setAttribute("numero", numero);
				numeroValide = true;
				System.out.println("bonjour");

				// Optionnel : charger l'employé correspondant si besoin
				// selectedEmploye = daoEmp.findByNumero(numero);
			} else {
				numeroValide = false;
				System.out.println("false");

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le numéro ne correspond pas."));
			}
		}
	}

	// Getters et Setters

	public List<Employe> getEmployes() {
		return employes;
	}

	public void setEmployes(List<Employe> employes) {
		this.employes = employes;
	}

	public Employe getSelectedEmploye() {
		return selectedEmploye;
	}

	public void setSelectedEmploye(Employe selectedEmploye) {
		this.selectedEmploye = selectedEmploye;
	}

	public List<Employe> getSelectedEmployes() {
		return selectedEmployes;
	}

	public void setSelectedEmployes(List<Employe> selectedEmployes) {
		this.selectedEmployes = selectedEmployes;
	}

	public List<Institution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(List<Institution> institutions) {
		this.institutions = institutions;
	}

	public List<Profile> getProfils() {
		return profils;
	}

	public void setProfils(List<Profile> profils) {
		this.profils = profils;
	}

	public int getInstitution() {
		return institution;
	}

	public void setInstitution(int institution) {
		this.institution = institution;
	}

	public int getIdProfil() {
		return idProfil;
	}

	public void setIdProfil(int idProfil) {
		this.idProfil = idProfil;
	}

	public int getIdDivision() {
		return idDivision;
	}

	public void setIdDivision(int idDivision) {
		this.idDivision = idDivision;
	}

	public boolean hasSelectedEmployes() {
		return selectedEmployes != null && !selectedEmployes.isEmpty();
	}

	/**
	 * Renvoie la liste des divisions correspondant à l'institution choisie. Si
	 * l'employé sélectionné possède déjà une institution, celle-ci sera utilisée ;
	 * sinon, la valeur de la propriété 'institution' (sélectionnée dans le
	 * formulaire) sera prise.
	 */
	public List<Division> getDivisionsByInstitution() {
		List<Division> divisionsByInstitution = new ArrayList<>();
		int instId = 0;
		if (selectedEmploye != null && selectedEmploye.getInstitution() != null) {
			instId = selectedEmploye.getInstitution().getIdins();
		} else {
			instId = institution;
		}
		if (instId != 0) {
			for (Division d : divisions) {
				if (d.getInstitution() != null && d.getInstitution().getIdins() == instId) {
					divisionsByInstitution.add(d);
				}
			}
		}
		return divisionsByInstitution;
	}

	// Ouvre le formulaire pour créer un nouvel employé
	public void openNew() {
		selectedEmploye = new Employe();
		selectedEmploye.setId(new EmployeId());
		selectedEmploye.setPersonne(new Personne());
	}

	/**
	 * Méthode pour fusionner l'affiliation de division à l'employé. Elle récupère
	 * la division sélectionnée et l'affecte à l'employé.
	 */
	public void merge() {
		if (selectedEmploye.getId() != null) {
			Division selectedDivision = daoDivision.findById(idDivision);
			selectedEmploye.setDivision(selectedDivision);
			daoEmp.merge(selectedEmploye);
		}
	}

	// Sauvegarde ou mise à jour de l'employé
	public void saveEmploye() {
		if (selectedEmploye.getId() == null || selectedEmploye.getId().getIdemploye() == 0) {
			// Création d'un nouvel employé
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(selectedEmploye.getPersonne().getMotpasse());
			selectedEmploye.getPersonne().setMotpasse(hashedPassword);
			// Affecter le profil par défaut ou en fonction de idProfil
			selectedEmploye.getPersonne().setProfile(daoProfile.findByProfile("employe"));
			daoPer.persist(selectedEmploye.getPersonne());
			selectedEmploye.setInstitution(daoInstitution.findById(institution));
			Institution i=daoInstitution.findById(institution);
			i.setNombremembres(1+1);
			daoInstitution.merge(i);
			employeId.setIdpersonne(selectedEmploye.getPersonne().getIdpersonne());
			employeId.setIdemploye(daoEmp.getNextSequenceValue());
			selectedEmploye.setId(employeId);
			selectedEmploye.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			daoEmp.persist(selectedEmploye);
			FacesContext context = FacesContext.getCurrentInstance();
			String message = context.getApplication().evaluateExpressionGet(context, "#{msg['reussi']}", String.class);
			FacesMessage facesMessage = new FacesMessage(message);
			context.addMessage(null, facesMessage);
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé ajouté"));
		} else {
			// Mise à jour d'un employé existant
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(selectedEmploye.getPersonne().getMotpasse());
			if (hashedPassword != null) {
				selectedEmploye.getPersonne().setMotpasse(hashedPassword);
			}
			if (idProfil != 0) {
				selectedEmploye.setInstitution(daoInstitution.findById(institution));
				selectedEmploye.getPersonne().setProfile(daoProfile.findById(idProfil));
			}
			daoPer.merge(selectedEmploye.getPersonne());
			daoEmp.merge(selectedEmploye);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé mis à jour"));
		}
		employes = daoEmp.findAll();
		PrimeFaces.current().executeScript("PF('manageEmployeDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-employe");
	}
	
	public String saveEmploye1() {
/*		if (selectedEmploye.getId() == null || selectedEmploye.getId().getIdemploye() == 0) {
*/			// Création d'un nouvel employé
			numero=(int) session.getAttribute("numero");
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(selectedEmploye.getPersonne().getMotpasse());
			selectedEmploye.getPersonne().setMotpasse(hashedPassword);
			// Affecter le profil par défaut ou en fonction de idProfil
			selectedEmploye.getPersonne().setProfile(daoProfile.findByProfile("employe"));
			daoPer.persist(selectedEmploye.getPersonne());
			selectedEmploye.setInstitution(daoInstitution.findById(institution));
			selectedEmploye.setDivision(daoDivision.findById(idDivision));;
			employeId.setIdpersonne(selectedEmploye.getPersonne().getIdpersonne());
			employeId.setIdemploye(daoEmp.getNextSequenceValue());
			selectedEmploye.setId(employeId);
			selectedEmploye.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			daoEmp.persist(selectedEmploye);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé ajouté"));
			/*
			 * } else { // Mise à jour d'un employé existant BCryptPasswordEncoder
			 * passwordEncoder = new BCryptPasswordEncoder(); String hashedPassword =
			 * passwordEncoder.encode(selectedEmploye.getPersonne().getMotpasse()); if
			 * (hashedPassword != null) {
			 * selectedEmploye.getPersonne().setMotpasse(hashedPassword); } if (idProfil !=
			 * 0) { selectedEmploye.setInstitution(daoInstitution.findById(institution));
			 * selectedEmploye.getPersonne().setProfile(daoProfile.findById(idProfil)); }
			 * daoPer.merge(selectedEmploye.getPersonne()); daoEmp.merge(selectedEmploye);
			 * FacesContext.getCurrentInstance().addMessage(null, new
			 * FacesMessage("Employé mis à jour"));
			 
		}*/
		employes = daoEmp.findAll();
		PrimeFaces.current().executeScript("PF('manageEmployeDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-employe");
		return "login";
	}

	public void saveAdmin() {
		if (selectedEmploye.getId() == null || selectedEmploye.getId().getIdemploye() == 0) {
			// Création d'un nouvel employé
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(selectedEmploye.getPersonne().getMotpasse());
			selectedEmploye.getPersonne().setMotpasse(hashedPassword);
			// Affecter le profil par défaut ou en fonction de idProfil
			selectedEmploye.getPersonne().setProfile(daoProfile.findByProfile("admin"));
			selectedEmploye.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			daoPer.persist(selectedEmploye.getPersonne());
			/*
			 * selectedEmploye.setInstitution(daoInstitution.findById(institution));
			 * employeId.setIdpersonne(selectedEmploye.getPersonne().getIdpersonne());
			 * employeId.setIdemploye(daoEmp.getNextSequenceValue());
			 * selectedEmploye.setId(employeId); daoEmp.persist(selectedEmploye);
			 */
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé ajouté"));
		} else {
			// Mise à jour d'un employé existant
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(selectedEmploye.getPersonne().getMotpasse());
			if (hashedPassword != null) {
				selectedEmploye.getPersonne().setMotpasse(hashedPassword);
			}
			if (idProfil != 0) {
				selectedEmploye.setInstitution(daoInstitution.findById(institution));
				selectedEmploye.getPersonne().setProfile(daoProfile.findById(idProfil));
			}
			daoPer.merge(selectedEmploye.getPersonne());
			daoEmp.merge(selectedEmploye);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé mis à jour"));
		}
		employes = daoEmp.findAll();
		PrimeFaces.current().executeScript("PF('manageEmployeDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-employe");
	}

	// Suppression d'un employé sélectionné
	public void deleteEmploye() {
		if (selectedEmploye != null) {
			daoEmp.remove(selectedEmploye);
			daoPer.remove(selectedEmploye.getPersonne());
			employes = daoEmp.findAll();
			FacesContext context = FacesContext.getCurrentInstance();
			String message = context.getApplication().evaluateExpressionGet(context, "#{msg['suppprime']}", String.class);
			FacesMessage facesMessage = new FacesMessage(message);
			context.addMessage(null, facesMessage);
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employé supprimé"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-employe");
			selectedEmploye = null;
		}
	}

	// Suppression des employés sélectionnés
	public void deleteSelectedEmployes() {
		if (selectedEmployes != null && !selectedEmployes.isEmpty()) {
			for (Employe emp : selectedEmployes) {
				daoEmp.remove(emp);
			}
			employes = daoEmp.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employés supprimés"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-employe");
			selectedEmployes = null;
		}
	}

	// Message dynamique pour le bouton de suppression multiple
	public String getDeleteButtonMessage() {
		if (selectedEmployes != null && !selectedEmployes.isEmpty()) {
			int size = selectedEmployes.size();
			return size > 1 ? size + " employés sélectionnés" : "1 employé sélectionné";
		}
		return "Delete";
	}

	public List<Employe> getAdmin() {
		return admin;
	}

	public void setAdmin(List<Employe> admin) {
		this.admin = admin;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public boolean isNumeroValide() {
		return numeroValide;
	}

	public void setNumeroValide(boolean numeroValide) {
		this.numeroValide = numeroValide;
	}

	public List<Division> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<Division> divisions) {
		this.divisions = divisions;
	}
}
