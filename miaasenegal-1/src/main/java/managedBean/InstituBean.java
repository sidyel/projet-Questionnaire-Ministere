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
import org.entitee.Institution;
import org.entitee.Categorieinstitution;
import org.primefaces.PrimeFaces;

@Named("instituBean")
@ViewScoped
public class InstituBean implements Serializable {

    private List<Institution> institutions = new ArrayList<>();
    private Institution selectedInstitution;
    private Categorieinstitution categorieinstitution;
    private List<Institution> selectedInstitutions = new ArrayList<>();
    private int categorieId;
    
    GenericDAO<Institution> daoInst = new GenericDAO<>(Institution.class);
    GenericDAO<Categorieinstitution> daoCat = new GenericDAO<>(Categorieinstitution.class);
    
    @PostConstruct
    public void init() {
        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("id");
        try {
            categorieId = Integer.parseInt(idParam);
        } catch(Exception e) {
            categorieId = 0;
        }
        if (categorieId != 0) {
        	categorieinstitution=daoCat.findById(categorieId);
            institutions = daoInst.findinstitution(categorieId);
        } else {
            institutions = new ArrayList<>();
        }
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
    
    public int getCategorieId() {
        return categorieId;
    }
    
    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }
    
    public String getDeleteButtonMessage() {
        if (selectedInstitutions != null && !selectedInstitutions.isEmpty()) {
            int size = selectedInstitutions.size();
            return size > 1 ? size + " institutions sélectionnées" : "1 institution sélectionnée";
        }
        return "Delete";
    }
    
    // Actions CRUD
    public void openNew() {
        selectedInstitution = new Institution();
        // Associer la catégorie courante à la nouvelle institution
        selectedInstitution.setCategorieinstitution(daoCat.findById(categorieId));
    }
    
    public void saveInstitution() {
        if (selectedInstitution.getIdins() == 0) {
        	categorieinstitution.setNombreelements(categorieinstitution.getNombreelements()+1);
        	daoCat.merge(categorieinstitution);
        	selectedInstitution.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); 
        	selectedInstitution.setNombremembres(0);
            daoInst.persist(selectedInstitution);
            institutions = daoInst.findinstitution(categorieId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution ajoutée"));
        } else {
            // Logique de mise à jour si nécessaire
        	daoInst.merge(selectedInstitution);
            institutions = daoInst.findinstitution(categorieId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution mise à jour"));
        }
        institutions = daoInst.findinstitution(categorieId);
        PrimeFaces.current().executeScript("PF('manageInstDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
    }
    
    public void deleteInstitution() {
        if (selectedInstitution != null) {
        	categorieinstitution.setNombreelements(categorieinstitution.getNombreelements()-1);
        	daoCat.merge(categorieinstitution);
            daoInst.remove(selectedInstitution);
            institutions.remove(selectedInstitution);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institution supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
            selectedInstitution = null;
        }
    }
    
    public void deleteSelectedInstitutions() {
        if (selectedInstitutions != null && !selectedInstitutions.isEmpty()) {
            for (Institution inst : selectedInstitutions) {
                daoInst.remove(inst);
            }
            institutions.removeAll(selectedInstitutions);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Institutions supprimées"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-institutions");
            selectedInstitutions = new ArrayList<>();
        }
    }

	public Categorieinstitution getCategorieinstitution() {
		return categorieinstitution;
	}

	public void setCategorieinstitution(Categorieinstitution categorieinstitution) {
		this.categorieinstitution = categorieinstitution;
	}
}
