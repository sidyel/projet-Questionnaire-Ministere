package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.dao.GenericDAO;
import org.entitee.Permission;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class PermissionView implements Serializable {

    private List<Permission> permissions;
    private Permission selectedPermission;
    private List<Permission> selectedPermissions;
    private GenericDAO<Permission> daoPermission = new GenericDAO<>(Permission.class);

    @PostConstruct
    public void init() {
        permissions = daoPermission.findAll();
    }

    // Getters et setters
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Permission getSelectedPermission() {
        return selectedPermission;
    }

    public void setSelectedPermission(Permission selectedPermission) {
        this.selectedPermission = selectedPermission;
    }

    public List<Permission> getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(List<Permission> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    // Méthode utilitaire pour vérifier la sélection
    public boolean hasSelectedPermissions() {
        return selectedPermissions != null && !selectedPermissions.isEmpty();
    }

    // Ouvre un formulaire de création
    public void openNew() {
        selectedPermission = new Permission();
    }

    // Sauvegarde ou met à jour une permission
    public void savePermission() {
        if (selectedPermission.getIdpermission() == 0) { // nouvelle permission
        	selectedPermission.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            daoPermission.persist(selectedPermission);
            permissions = daoPermission.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permission ajoutée"));
        } else {
        	daoPermission.merge(selectedPermission);
            permissions = daoPermission.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permission mise à jour"));
        }
        PrimeFaces.current().executeScript("PF('managePermissionDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-permission");
    }

    // Supprime la permission sélectionnée
    public void deletePermission() {
        if (selectedPermission != null) {
            daoPermission.remove(selectedPermission);
            permissions = daoPermission.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permission supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-permission");
            selectedPermission = null;
        }
    }

    // Supprime les permissions sélectionnées
    public void deleteSelectedPermissions() {
        if (selectedPermissions != null && !selectedPermissions.isEmpty()) {
            // Supprimer chaque permission de la BDD
            for (Permission perm : selectedPermissions) {
                daoPermission.remove(perm);
                permissions = daoPermission.findAll();
            }
            permissions.removeAll(selectedPermissions);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permissions supprimées"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-permission");
            selectedPermissions = null;
        }
    }

    // Message dynamique pour le bouton de suppression multiple
    public String getDeleteButtonMessage() {
        if (selectedPermissions != null && !selectedPermissions.isEmpty()) {
            int size = selectedPermissions.size();
            return size > 1 ? size + " permissions sélectionnées" : "1 permission sélectionnée";
        }
        return "Supprimer";
    }
}
