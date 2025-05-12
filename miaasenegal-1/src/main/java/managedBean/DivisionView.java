package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.entitee.Division;
import org.entitee.Institution;
import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;

@Named
@ViewScoped
public class DivisionView implements Serializable {

	private List<Division> divisions  = new ArrayList<>();;
	private Division selectedDivision;
	private List<Division> selectedDivisions;
	private List<Institution> institutions = new ArrayList<Institution>();

	public List<Institution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(List<Institution> institutions) {
		this.institutions = institutions;
	}

	// Institution par défaut pour l'association
	private int institution;

	GenericDAO<Division> daoDivision = new GenericDAO<>(Division.class);
	GenericDAO<Institution> daoInstitution = new GenericDAO<>(Institution.class);

	@PostConstruct
	public void init() {
		// Création d'une institution fictive pour l'exemple

		institutions = daoInstitution.findAll();
		
		divisions=daoDivision.findAll();
		// Ajout de quelques divisions d'exemple

	}

	// Getters et setters
	public List<Division> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<Division> divisions) {
		this.divisions = divisions;
	}

	public Division getSelectedDivision() {
		return selectedDivision;
	}

	public void setSelectedDivision(Division selectedDivision) {
		this.selectedDivision = selectedDivision;
	}

	public List<Division> getSelectedDivisions() {
		return selectedDivisions;
	}

	public void setSelectedDivisions(List<Division> selectedDivisions) {
		this.selectedDivisions = selectedDivisions;
	}

	// Vérifie si des divisions sont sélectionnées
	public boolean hasSelectedDivisions() {
		return selectedDivisions != null && !selectedDivisions.isEmpty();
	}

	// Ouvre le formulaire pour créer une nouvelle division
	public void openNew() {
		selectedDivision = new Division();
		// Affecte l'institution par défaut à la nouvelle division
		// selectedDivision.setInstitution(institution);
	}

	public int getInstitution() {
		return institution;
	}

	public void setInstitution(int institution) {
		this.institution = institution;
	}

	// Sauvegarde ou met à jour une division
	public void saveDivision() {
		if (selectedDivision.getIddivision() == 0) { // Nouvelle division
			// selectedDivision.setIddivision(generateNewId());
			selectedDivision.setInstitution(daoInstitution.findById(institution));
			selectedDivision.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			daoDivision.persist(selectedDivision);
			divisions=daoDivision.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Division added"));
		} else {
			daoDivision.merge(selectedDivision);
			divisions=daoDivision.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Division updated"));
		}
		PrimeFaces.current().executeScript("PF('manageDivisionDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-divisions");
	}
	
	public void mergeDivision() {
		if (selectedDivision.getIddivision() == 0) { // Nouvelle division
			// selectedDivision.setIddivision(generateNewId());
			selectedDivision.setInstitution(daoInstitution.findById(institution));
			daoDivision.merge(selectedDivision);
			divisions.add(selectedDivision);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Division added"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Division updated"));
		}
		PrimeFaces.current().executeScript("PF('manageDivisionDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-divisions");
	}

	// Supprime la division sélectionnée
	public void deleteDivision() {
		if (selectedDivision != null) {
			daoDivision.remove(selectedDivision);
			divisions=daoDivision.findAll();

FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Division deleted"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-divisions");
			selectedDivision = null;
		}
	}

	// Supprime les divisions sélectionnées
	public void deleteSelectedDivisions() {
		if (selectedDivisions != null && !selectedDivisions.isEmpty()) {
			daoDivision.remove(selectedDivision);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Divisions deleted"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-divisions");
			selectedDivisions = null;
		}
	}

	// Message dynamique pour le bouton de suppression multiple
	public String getDeleteButtonMessage() {
		if (selectedDivisions != null && !selectedDivisions.isEmpty()) {
			int size = selectedDivisions.size();
			return size > 1 ? size + " divisions selected" : "1 division selected";
		}
		return "Delete";
	}

	// Méthode utilitaire pour générer un nouvel identifiant (simulation)
	private int generateNewId() {
		int maxId = 0;
		for (Division d : divisions) {
			if (d.getIddivision() > maxId) {
				maxId = d.getIddivision();
			}
		}
		return maxId + 1;
	}
}
