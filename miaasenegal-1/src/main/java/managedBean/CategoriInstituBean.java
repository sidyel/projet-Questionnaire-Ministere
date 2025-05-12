package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;
import org.primefaces.PrimeFaces;

@Named("categoriInstituBean")
@ViewScoped
public class CategoriInstituBean implements Serializable {

    private List<Categorieinstitution> categories;
    private Categorieinstitution selectedCategory;
    private List<Categorieinstitution> selectedCategories = new ArrayList<>();

    GenericDAO<Categorieinstitution> daoCat = new GenericDAO<>(Categorieinstitution.class);

    @PostConstruct
    public void init() {
        categories = daoCat.findAll();
    }

    // Getters et setters
    public List<Categorieinstitution> getCategories() {
        return categories;
    }

    public void setCategories(List<Categorieinstitution> categories) {
        this.categories = categories;
    }

    public Categorieinstitution getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Categorieinstitution selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<Categorieinstitution> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<Categorieinstitution> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public String getDeleteButtonMessage() {
        if (selectedCategories != null && !selectedCategories.isEmpty()) {
            int size = selectedCategories.size();
            return size > 1 ? size + " catégories sélectionnées" : "1 catégorie sélectionnée";
        }
        return "Delete";
    }

    // Actions CRUD
    public void openNew() {
        selectedCategory = new Categorieinstitution();
    }

    public void saveCategory() {
        if (selectedCategory.getIdcatins() == 0) {
        	selectedCategory.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); 
        	selectedCategory.setNombreelements(0);
            daoCat.persist(selectedCategory);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie ajoutée"));
        } else {
            // Logique de mise à jour si nécessaire
        	selectedCategory.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); 
        	daoCat.merge(selectedCategory);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie mise à jour"));
        }
        categories = daoCat.findAll();
        PrimeFaces.current().executeScript("PF('manageCatDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-categories");
    }

    public void deleteCategory() {
        if (selectedCategory != null) {
            daoCat.remove(selectedCategory);
            categories.remove(selectedCategory);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégorie supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-categories");
            selectedCategory = null;
        }
    }

    public void deleteSelectedCategories() {
        if (selectedCategories != null && !selectedCategories.isEmpty()) {
            for (Categorieinstitution cat : selectedCategories) {
                daoCat.remove(cat);
            }
            categories.removeAll(selectedCategories);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Catégories supprimées"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-categories");
            selectedCategories = new ArrayList<>();
        }
    }

    // Redirection vers le CRUD des institutions pour la catégorie sélectionnée
    public String redirectToInstitutions(Categorieinstitution cat) {
        return "gererInstitution.xhtml?faces-redirect=true&id=" + cat.getIdcatins();
    }
}
