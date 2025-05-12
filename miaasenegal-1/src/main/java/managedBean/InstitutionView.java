package managedBean; // Veuillez adapter le package à votre projet

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;
import org.entitee.Institution;

@Named
@ViewScoped
public class InstitutionView implements Serializable {

    private List<Institution> institutions;
    private Institution selectedInstitution;
    private List<Institution> selectedInstitutions;
    private List<Categorieinstitution> categorieinstitutions=new ArrayList<Categorieinstitution>();
    private int categorieinstitution;
    public int getCategorieinstitution() {
		return categorieinstitution;
	}

	public void setCategorieinstitution(int categorieinstitution) {
		this.categorieinstitution = categorieinstitution;
	}

	public List<Categorieinstitution> getCategorieinstitutions() {
		return categorieinstitutions;
	}

	public void setCategorieinstitutions(List<Categorieinstitution> categorieinstitutions) {
		this.categorieinstitutions = categorieinstitutions;
	}

	GenericDAO<Institution> daoinstitution =new GenericDAO<>(Institution.class);
	GenericDAO<Categorieinstitution> daoCatIns =new GenericDAO<>(Categorieinstitution.class);

    @PostConstruct
    public void init() {
        institutions = new ArrayList<>();
        Categorieinstitution dummyCat = new Categorieinstitution(1, "Catégorie A", 101, "2025-02-27", 5, new HashSet<>());
        categorieinstitutions=daoCatIns.findAll();
        institutions=daoinstitution.findAll();
        // Simulation de quelques institutions
      
    }

    // Getters et setters
    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public Institution getSelectedInstitution() {
        return selectedInstitution;
    }

    public void setSelectedInstitution(Institution selectedInstitution) {
        this.selectedInstitution = selectedInstitution;
    }

    public List<Institution> getSelectedInstitutions() {
        return selectedInstitutions;
    }

    public void setSelectedInstitutions(List<Institution> selectedInstitutions) {
        this.selectedInstitutions = selectedInstitutions;
    }

    // Vérifie si des institutions sont sélectionnées
   
    public boolean hasSelectedInstitutions() {
        return selectedInstitutions != null && !selectedInstitutions.isEmpty();
    }
    public void openNew() {
        // Crée une nouvelle instance ; vous pouvez initialiser la catégorie par défaut si besoin
        selectedInstitution = new Institution();
      
    }

    // Sauvegarde ou met à jour une institution
    public void saveInstitution() {
        if (selectedInstitution.getIdins() == 0) { // Nouvelle institution
            //selectedInstitution.setIdins(generateNewId());
        	selectedInstitution.setCategorieinstitution(daoCatIns.findById(categorieinstitution));
        	daoinstitution.persist(selectedInstitution);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution ajoutée"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution mise à jour"));
        }
        PrimeFaces.current().executeScript("PF('manageInstitutionDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
    }
    
    public void mergeIns() {
        if (selectedInstitution.getIdins() == 0) { // Nouvelle institution
            //selectedInstitution.setIdins(generateNewId());
        	selectedInstitution.setCategorieinstitution(daoCatIns.findById(categorieinstitution));
        	daoinstitution.merge(selectedInstitution);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution ajoutée"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution mise à jour"));
        }
        PrimeFaces.current().executeScript("PF('manageInstitutionDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
    }

    // Supprime l'institution sélectionnée
    public void deleteInstitution() {
        if (selectedInstitution != null) {
        	daoinstitution.remove(selectedInstitution);
            institutions.remove(selectedInstitution);
            daoinstitution.persist(selectedInstitution);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
            selectedInstitution = null;
        }
    }

    // Supprime les institutions sélectionnées
    public void deleteSelectedInstitutions() {
        if (selectedInstitutions != null && !selectedInstitutions.isEmpty()) {
            institutions.removeAll(selectedInstitutions);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institutions supprimées"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
            selectedInstitutions = null;
        }
    }

    // Message dynamique pour le bouton de suppression multiple
    public String getDeleteButtonMessage() {
        if (selectedInstitutions != null && !selectedInstitutions.isEmpty()) {
            int size = selectedInstitutions.size();
            return size > 1 ? size + " institutions sélectionnées" : "1 institution sélectionnée";
        }
        return "Delete";
    }

    // Méthode utilitaire pour générer un nouvel identifiant (simulation)
    private int generateNewId() {
        int maxId = 0;
        for (Institution ins : institutions) {
            if (ins.getIdins() > maxId) {
                maxId = ins.getIdins();
            }
        }
        return maxId + 1;
    }
}
