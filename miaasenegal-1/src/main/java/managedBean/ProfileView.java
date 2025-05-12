package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.dao.GenericDAO;
import org.entitee.Profile;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class ProfileView implements Serializable {

    private List<Profile> profiles;
    private Profile selectedProfile;
    private List<Profile> selectedProfiles;
    
    private GenericDAO<Profile> daoProfile = new GenericDAO<>(Profile.class);
    
    @PostConstruct
    public void init() {
        profiles = daoProfile.findAll();
        
    }

    // Getters et setters
    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(Profile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public List<Profile> getSelectedProfiles() {
        return selectedProfiles;
    }

    public void setSelectedProfiles(List<Profile> selectedProfiles) {
        this.selectedProfiles = selectedProfiles;
    }

    // Vérifie si des profils sont sélectionnés
    public boolean hasSelectedProfiles() {
        return selectedProfiles != null && !selectedProfiles.isEmpty();
    }

    // Ouvre le formulaire de création
    public void openNew() {
        selectedProfile = new Profile();
    }

    // Sauvegarde ou met à jour un profil
    public void saveProfile() {
        if (selectedProfile.getIdprofile() == 0) { // nouveau profil
        	selectedProfile.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            daoProfile.persist(selectedProfile);
            profiles = daoProfile.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profil ajouté"));
        } else {
        	  daoProfile.merge(selectedProfile);
              profiles = daoProfile.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profil mis à jour"));
        }
        PrimeFaces.current().executeScript("PF('manageProfileDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-profile");
    }

    // Supprime le profil sélectionné
    public void deleteProfile() {
        if (selectedProfile != null) {
            daoProfile.remove(selectedProfile);
            profiles = daoProfile.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profil supprimé"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-profile");
            selectedProfile = null;
        }
    }

    // Supprime les profils sélectionnés
    public void deleteSelectedProfiles() {
        if (selectedProfiles != null && !selectedProfiles.isEmpty()) {
            profiles.removeAll(selectedProfiles);
            for (Profile p : selectedProfiles) {
                daoProfile.remove(p);
                
            }
            profiles = daoProfile.findAll();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profils supprimés"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-profile");
            selectedProfiles = null;
        }
    }

    // Message dynamique pour le bouton de suppression multiple
    public String getDeleteButtonMessage() {
        if (selectedProfiles != null && !selectedProfiles.isEmpty()) {
            int size = selectedProfiles.size();
            return size > 1 ? size + " profils sélectionnés" : "1 profil sélectionné";
        }
        return "Delete";
    }

    // Méthode utilitaire pour générer un nouvel identifiant (optionnel selon la logique métier)
    private int generateNewId() {
        int maxId = 0;
        for (Profile p : profiles) {
            if (p.getIdprofile() > maxId) {
                maxId = p.getIdprofile();
            }
        }
        return maxId + 1;
    }
}
