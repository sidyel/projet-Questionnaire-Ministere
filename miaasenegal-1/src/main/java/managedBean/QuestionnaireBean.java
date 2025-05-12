package managedBean;

import javax.inject.Named;
import javax.persistence.GenerationType;
import javax.faces.view.ViewScoped;

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
import org.entitee.Questionnaire;
import org.entitee.Projet;
import org.dao.GenericDAO;
import org.entitee.Categorieinstitution;

@Named("questionnaireBean")
@ViewScoped
public class QuestionnaireBean implements Serializable {

	private List<Questionnaire> questionnaires;
	private List<Questionnaire> questionnaires1;

	private Questionnaire selectedQuestionnaire;
	private List<Questionnaire> selectedQuestionnaires=new ArrayList<Questionnaire>();
	private int idProject;
	GenericDAO<Questionnaire> daoQuest=new GenericDAO<>(Questionnaire.class);
	GenericDAO<Projet> daoProj = new GenericDAO<>(Projet.class);
	private List<Projet> projets=new ArrayList<>();
	// Projet fictif pour l'association (à remplacer par votre source de données)
	private Projet dummyProjet;

	@PostConstruct
	public void init() {
		String idParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("id");
        try {
        	idProject = Integer.parseInt(idParam);
        } catch(Exception e) {
        	idProject = 0;
        }
        if(idProject != 0) {
            setQuestionnaires1(daoQuest.findQuestionnnaireByproject(idProject));
        } else {
        	setQuestionnaires1(new ArrayList<>());
        }
		projets=daoProj.findAll();
		questionnaires =daoQuest.findAll();
		// Création d'un dummy projet avec sa catégorie
	
		// Forcer l'initialisation de l'institution afin d'éviter les problèmes lazy
        for (Questionnaire p : questionnaires) {
            p.getProjet().getLibelle();
        }

		
	}

	// Getters et setters
	public List<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(List<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}

	public Questionnaire getSelectedQuestionnaire() {
		return selectedQuestionnaire;
	}

	public void setSelectedQuestionnaire(Questionnaire selectedQuestionnaire) {
		this.selectedQuestionnaire = selectedQuestionnaire;
	}

	public List<Questionnaire> getSelectedQuestionnaires() {
		return selectedQuestionnaires;
	}

	public void setSelectedQuestionnaires(List<Questionnaire> selectedQuestionnaires) {
		this.selectedQuestionnaires = selectedQuestionnaires;
	}

	public boolean hasSelectedQuestionnaires() {
		return selectedQuestionnaires != null && !selectedQuestionnaires.isEmpty();
	}

	// Actions CRUD
	public void openNew() {
		selectedQuestionnaire = new Questionnaire();
		selectedQuestionnaire.setProjet(dummyProjet);
	}
	
	public void evaluationQuestionnaire(Questionnaire questionnaire) {
	    // Stocke l'ID dans la session pour le récupérer dans EvaluationBean
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("questionnaireId", questionnaire.getIdquestionnaire());
	    try {
	        FacesContext.getCurrentInstance().getExternalContext().redirect("evaluation.xhtml");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void ONjAMMAISrEPONDUQuestionnaire(Questionnaire questionnaire) {
	    // Stocke l'ID dans la session pour le récupérer dans EvaluationBean
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("questionnaireId", questionnaire.getIdquestionnaire());
	    try {
	        FacesContext.getCurrentInstance().getExternalContext().redirect("jamaisRepondu.xhtml");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	
	public void changerEtatQuestionnaire(Questionnaire q) {
		String etat=null;
		if(q!=null) {
			if(q.getEtat()!=null) {
				if(q.getEtat().equals("active")) {
					q.setEtat("desactive");
					etat="desactive";
				}
				else if(q.getEtat().equals("desactive")) {
					q.setEtat("active");
					etat="active";
				}
			}
			else {
				q.setEtat("active");
				etat="active";
			}
			daoQuest.merge(q);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaire  dans Etat "+etat));

		}
	}
	
	public void modifierEtat(Questionnaire qnn) {
	    if(qnn.getEtat() == null || qnn.getEtat().equalsIgnoreCase("desactive")){
	        qnn.setEtat("active");
	    } else {
	        qnn.setEtat("desactive");
	    }
	    daoQuest.merge(qnn);
	    // Recharger la liste des questionnaires pour refléter le changement
	    questionnaires = daoQuest.findAll();
	    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("L'état du questionnaire a été modifié"));
	}
	
	
	



	public void saveQuestionnaire() {
		if (selectedQuestionnaire.getIdquestionnaire() == 0) {
			selectedQuestionnaire.setProjet(daoProj.findById(idProject));
			selectedQuestionnaire.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			daoQuest.persist(selectedQuestionnaire);
			questionnaires=daoQuest.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaire ajouté"));
		} else {
			daoQuest.merge(selectedQuestionnaire);
			questionnaires=daoQuest.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaire mis à jour"));
		}
		PrimeFaces.current().executeScript("PF('manageQuestionnaireDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-questionnaires");
	}
	
	public void saveQuestionnaire1() {
		if (selectedQuestionnaire.getIdquestionnaire() == 0) {
			selectedQuestionnaire.setProjet(daoProj.findById(idProject));
			selectedQuestionnaire.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

			daoQuest.persist(selectedQuestionnaire);
            questionnaires = daoQuest.findQuestionnnaireByproject(idProject);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaire ajouté"));
		} else {
			daoQuest.merge(selectedQuestionnaire);
			questionnaires=daoQuest.findAll();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaire mis à jour"));
		}
		PrimeFaces.current().executeScript("PF('manageQuestionnaireDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-questionnaires");
	}
	
	

	public void deleteQuestionnaire() {
		if (selectedQuestionnaire != null) {
			daoQuest.remove(selectedQuestionnaire);
			questionnaires.remove(selectedQuestionnaire);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaire supprimé"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-questionnaires");
			selectedQuestionnaire = null;
		}
	}

	public void deleteSelectedQuestionnaires() {
		if (selectedQuestionnaires != null && !selectedQuestionnaires.isEmpty()) {
			questionnaires.removeAll(selectedQuestionnaires);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questionnaires supprimés"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-questionnaires");
			selectedQuestionnaires = null;
		}
	}

	public String getDeleteButtonMessage() {
		if (selectedQuestionnaires != null && !selectedQuestionnaires.isEmpty()) {
			int size = selectedQuestionnaires.size();
			return size > 1 ? size + " questionnaires sélectionnés" : "1 questionnaire sélectionné";
		}
		return "Delete";
	}

	
	public String redirectToQuestions(Questionnaire questionnaire) {
		return "questionCRUD.xhtml?faces-redirect=true&id=" + questionnaire.getIdquestionnaire();
	}
	
	public String redirectToQuestionsCreeEmploye(Questionnaire questionnaire) {
		return "QuestionCreeEmploye.xhtml?faces-redirect=true&id=" + questionnaire.getIdquestionnaire();
	}
	
	public String redirectToReponses(Questionnaire questionnaire) {
		return "gestionReponse.xhtml?faces-redirect=true&id=" + questionnaire.getIdquestionnaire();
	}

	public List<Projet> getProjets() {
		return projets;
	}

	public void setProjets(List<Projet> projets) {
		this.projets = projets;
	}

	public int getIdProject() {
		return idProject;
	}

	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}

	public List<Questionnaire> getQuestionnaires1() {
		return questionnaires1;
	}

	public void setQuestionnaires1(List<Questionnaire> questionnaires1) {
		this.questionnaires1 = questionnaires1;
	}

	
}
