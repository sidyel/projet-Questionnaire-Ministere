package managedBean; // Assurez-vous que le package correspond à votre configuration

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;
import org.entitee.Questionnaire;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class CategorieInstitutionView implements Serializable {

    private List<Categorieinstitution> categorieInstitutions;
    private Categorieinstitution selectedCat;
    private List<Categorieinstitution> selectedCats;
    GenericDAO<Categorieinstitution> daoCatInst = new GenericDAO<>(Categorieinstitution.class);

    @PostConstruct
    public void init() {
    	categorieInstitutions=daoCatInst.findAll();
        
    }

    // Getters et setters
    public List<Categorieinstitution> getCategorieInstitutions() {
        return categorieInstitutions;
    }

    public void setCategorieInstitutions(List<Categorieinstitution> categorieInstitutions) {
        this.categorieInstitutions = categorieInstitutions;
    }

    public Categorieinstitution getSelectedCat() {
        return selectedCat;
    }

    public void setSelectedCat(Categorieinstitution selectedCat) {
        this.selectedCat = selectedCat;
    }

    public List<Categorieinstitution> getSelectedCats() {
        return selectedCats;
    }

    public void setSelectedCats(List<Categorieinstitution> selectedCats) {
        this.selectedCats = selectedCats;
    }

    // Méthode appelée dans la page pour vérifier si des catégories ont été sélectionnées
    public boolean hasSelectedCats() {
        return selectedCats != null && !selectedCats.isEmpty();
    }

    // Ouvre un nouveau formulaire de création
    public void openNew() {
        selectedCat = new Categorieinstitution();
    }

    // Sauvegarde ou met à jour une catégorie
    public void saveCat() {
        if (selectedCat.getIdcatins() == 0) { // nouvelle catégorie
            //selectedCat.setIdcatins(generateNewId());
            daoCatInst.persist(selectedCat);
            categorieInstitutions.add(selectedCat);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie ajoutée"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie mise à jour"));
        }
        PrimeFaces.current().executeScript("PF('manageCatDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-cat");
    }
    
    public void mergeCat() {
        if (selectedCat.getIdcatins() == 0) { // nouvelle catégorie
            //selectedCat.setIdcatins(generateNewId());
            daoCatInst.merge(selectedCat);
            categorieInstitutions.add(selectedCat);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie ajoutée"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie mise à jour"));
        }
        PrimeFaces.current().executeScript("PF('manageCatDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-cat");
    }

    // Supprime la catégorie sélectionnée
    public void deleteCat() {
        if (selectedCat != null) {
            categorieInstitutions.remove(selectedCat);
            daoCatInst.remove(selectedCat);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cat");
            selectedCat = null;
        }
    }

    // Supprime les catégories sélectionnées
    public void deleteSelectedCats() {
        if (selectedCats != null && !selectedCats.isEmpty()) {
            categorieInstitutions.removeAll(selectedCats);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégories supprimées"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-cat");
            selectedCats = null;
        }
    }

    // Message dynamique pour le bouton de suppression multiple
    public String getDeleteButtonMessage() {
        if (selectedCats != null && !selectedCats.isEmpty()) {
            int size = selectedCats.size();
            return size > 1 ? size + " catégories sélectionnées" : "1 catégorie sélectionnée";
        }
        return "Delete";
    }
    
    
    public String redirectToinstitution(Categorieinstitution categorieinstitution1) {
		return "gererInstitution.xhtml?faces-redirect=true&id=" + categorieinstitution1.getIdcatins();
	}

    // Méthode utilitaire pour générer un nouvel identifiant (simulation)
    private int generateNewId() {
        int maxId = 0;
        for (Categorieinstitution cat : categorieInstitutions) {
            if (cat.getIdcatins() > maxId) {
                maxId = cat.getIdcatins();
            }
        }
        return maxId + 1;
    }
}
