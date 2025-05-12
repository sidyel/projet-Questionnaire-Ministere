package managedBean; // Adaptez le package à votre configuration

import javax.inject.Named;
import javax.faces.view.ViewScoped;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.primefaces.model.file.UploadedFile;
import org.entitee.Projet;
import org.entitee.Question;
import org.entitee.Institution;
import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;
import org.entitee.Employe;

@Named
@ViewScoped
public class ProjetView implements Serializable {

	private List<Projet> projets;
	private List<Projet> projets1;

	private Projet selectedProjet;
	private List<Projet> selectedProjets;
	private int institution;
	private UploadedFile uploadedFile;

	private List<Institution> institutions = new ArrayList<Institution>();
	private Institution dummyInstitution;
	GenericDAO<Projet> daoProjet = new GenericDAO<>(Projet.class);
	GenericDAO<Institution> daoIns = new GenericDAO<>(Institution.class);
	GenericDAO<Employe> daoEmp = new GenericDAO<>(Employe.class);

	private Employe currentEmploye;

	@PostConstruct
	public void init() {
		// Création d'une institution fictive avec sa catégorie
		setCurrentEmploye(
				(Employe) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("employe"));
		institutions = daoIns.findAll();
		projets = daoProjet.findAll();
		setProjets1(daoProjet.listeProjet(currentEmploye.getInstitution().getIdins()));
		for (Projet p : projets) {
			p.getInstitution().getLibelle();
		}
	}

	// Getters et setters
	public List<Projet> getProjets() {
		return projets;
	}

	public void setProjets(List<Projet> projets) {
		this.projets = projets;
	}

	public Projet getSelectedProjet() {
		return selectedProjet;
	}

	public void setSelectedProjet(Projet selectedProjet) {
		this.selectedProjet = selectedProjet;
	}

	public List<Projet> getSelectedProjets() {
		return selectedProjets;
	}

	public void setSelectedProjets(List<Projet> selectedProjets) {
		this.selectedProjets = selectedProjets;
	}

	// Vérifie si des projets sont sélectionnés (pour activer/désactiver le bouton
	// Delete)
	public boolean hasSelectedProjets() {
		return selectedProjets != null && !selectedProjets.isEmpty();
	}

	// Ouvre le formulaire pour créer un nouveau projet
	public void openNew() {
		selectedProjet = new Projet();
		// Affecte l'institution par défaut au nouveau projet
		selectedProjet.setInstitution(dummyInstitution);
	}

	// Sauvegarde ou met à jour un projet
	public void saveProjet() {
		if (selectedProjet.getIdprojet() == 0) {
			if (uploadedFile != null) {
				try {
					// Enregistrer le fichier sur le serveur
					String filePath = saveUploadedFile(uploadedFile);
					selectedProjet.setPhoto(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			selectedProjet.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			selectedProjet.setInstitution(daoIns.findById(institution));
			daoProjet.persist(selectedProjet);
			projets = daoProjet.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projet ajouté"));
		} else {
			daoProjet.merge(selectedProjet);
			projets = daoProjet.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projet mis à jour"));
		}
		PrimeFaces.current().executeScript("PF('manageProjetDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projets");
	}

	public void saveProjet1() {
		if (selectedProjet.getIdprojet() == 0) {
			if (uploadedFile != null) {
				try {
					// Enregistrer le fichier sur le serveur
					String filePath = saveUploadedFile(uploadedFile);
					selectedProjet.setPhoto(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			selectedProjet.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			selectedProjet.setInstitution(currentEmploye.getInstitution());
			daoProjet.persist(selectedProjet);
			projets = daoProjet.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projet ajouté"));
		} else {
			daoProjet.merge(selectedProjet);
			projets = daoProjet.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projet mis à jour"));
		}
		PrimeFaces.current().executeScript("PF('manageProjetDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projets");
	}

	// Supprime le projet sélectionné
	public void deleteProjet() {
		if (selectedProjet != null) {
			daoProjet.remove(selectedProjet);
			projets = daoProjet.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projet supprimé"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-projets");
			selectedProjet = null;
		}
	}

	// Supprime les projets sélectionnés
	public void deleteSelectedProjets() {
		if (selectedProjets != null && !selectedProjets.isEmpty()) {
			for (Projet emp : selectedProjets) {
				daoProjet.remove(emp);
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projets supprimés"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-projets");
			selectedProjets = null;
		}
	}

	// Message dynamique pour le bouton de suppression multiple
	public String getDeleteButtonMessage() {
		if (selectedProjets != null && !selectedProjets.isEmpty()) {
			int size = selectedProjets.size();
			return size > 1 ? size + " projets sélectionnés" : "1 projet sélectionné";
		}
		return "Delete";
	}

	// Génère un nouvel identifiant (simulation)
	private int generateNewId() {
		int maxId = 0;
		for (Projet p : projets) {
			if (p.getIdprojet() > maxId) {
				maxId = p.getIdprojet();
			}
		}
		return maxId + 1;
	}

	public int getInstitution() {
		return institution;
	}

	public void setInstitution(int institution) {
		this.institution = institution;
	}

	public List<Institution> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(List<Institution> institutions) {
		this.institutions = institutions;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	private String saveUploadedFile(UploadedFile file) throws IOException {
		// Définir le chemin du fichier
		String directory = "C:/Users/dione/Documents/workspace/workspasce-MNdong/miaasenegal-1/src/main/webapp/resources/poseidon-layout/images";
		String filePath = directory + file.getFileName();

		// Vérifier si le répertoire existe, sinon le créer
		File uploadDir = new File(directory);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		// Sauvegarder le fichier sur le disque
		File outputFile = new File(filePath);
		try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
			outputStream.write(file.getContent());
		}

		return file.getFileName(); // Retourner le chemin du fichier sauvegardé
	}

	public Employe getCurrentEmploye() {
		return currentEmploye;
	}

	public void setCurrentEmploye(Employe currentEmploye) {
		this.currentEmploye = currentEmploye;
	}

	public List<Projet> getProjets1() {
		return projets1;
	}

	public void setProjets1(List<Projet> projets1) {
		this.projets1 = projets1;
	}
}
