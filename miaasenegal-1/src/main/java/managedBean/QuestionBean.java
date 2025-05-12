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
import org.entitee.Employe;
import org.entitee.Question;
import org.entitee.Questionnaire;
import org.entitee.Reponse;
// Importez votre GenericDAO ou service si nécessaire
import org.primefaces.PrimeFaces;

@Named("questionBean")
@ViewScoped
public class QuestionBean implements Serializable {

    private List<Question> questions=new ArrayList<Question>();
    private Question selectedQuestion;
    private List<Question> selectedQuestions;
    private int questionnaireId;
    
    GenericDAO<Question> daoQues =new GenericDAO<>(Question.class);
    GenericDAO<Questionnaire> daoQuestinnaire=new GenericDAO<>(Questionnaire.class);
    
    List<Reponse> reponses=new ArrayList<Reponse>();
    List<Reponse> selecedreponses=new ArrayList<Reponse>();

    
   
    
    @PostConstruct
    public void init() {
        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("id");
        try {
            questionnaireId = Integer.parseInt(idParam);
        } catch(Exception e) {
            questionnaireId = 0;
        }
        if(questionnaireId != 0) {
            questions = daoQues.findQuestionsByQuestionnaire(questionnaireId);
            reponses=daoQues.trouverReponsesDistinctesParPersonnePourQuestionnaire(questionnaireId);
        } else {
            questions = new ArrayList<>();
        }
    }
    
    public void afficherReponsesParEmploye(int idPersonne) {
    	selecedreponses=daoQues.trouverReponsesPourQuestionnaireEtUser(questionnaireId, idPersonne);
    }

    public boolean isHasSelectedQuestions() {
        return selectedQuestions != null && !selectedQuestions.isEmpty();
    }

    // Getters et setters
    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    public Question getSelectedQuestion() {
        return selectedQuestion;
    }
    public void setSelectedQuestion(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
    }
    public List<Question> getSelectedQuestions() {
        return selectedQuestions;
    }
    public void setSelectedQuestions(List<Question> selectedQuestions) {
        this.selectedQuestions = selectedQuestions;
    }
    public int getQuestionnaireId() {
        return questionnaireId;
    }
    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }
    
    // Méthode pour vérifier si des questions sont sélectionnées (pour activer le bouton Delete)
    public boolean hasSelectedQuestions() {
        return selectedQuestions != null && !selectedQuestions.isEmpty();
    }
    
    // Exemple d'actions CRUD (à compléter selon vos besoins)
    public void openNew() {
        selectedQuestion = new Question();
        // Associer le questionnaire courant au niveau de la persistance si besoin
    }
    
    public String redirectToQuestions(Question questionnaire) {
		return "itemQuestionCRUD.xhtml?faces-redirect=true&id=" + questionnaire.getIdquestion();
	}
    
    public String redirectToItemsQuestions(Question questionnaire) {
		return "itemsCreeEmploye.xhtml?faces-redirect=true&id=" + questionnaire.getIdquestion();
	}
    
    public void saveQuestion() {
        if(selectedQuestion.getIdquestion() == 0) {
            // Affecter un ID, persister la question, etc.
        	selectedQuestion.setDatecreation(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			/*
			 * if(selectedQuestion.getNumeroordre()==null) {
			 * selectedQuestion.setNumeroordre(1); } else {
			 * selectedQuestion.setNumeroordre(selectedQuestion.getNumeroordre()+1);
			 * 
			 * }
			 */
        	selectedQuestion.setQuestionnaire(daoQuestinnaire.findById(questionnaireId));
            daoQues.persist(selectedQuestion);
            questions=daoQues.findQuestionsByQuestionnaire(questionnaireId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Question ajoutée"));
        } else {
        	 daoQues.merge(selectedQuestion);
             questions=daoQues.findQuestionsByQuestionnaire(questionnaireId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Question mise à jour"));
        }
        PrimeFaces.current().executeScript("PF('manageQuestionDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-questions");
    }
    
    public void deleteQuestion() {
        if(selectedQuestion != null) {
            daoQues.remove(selectedQuestion);
            questions=daoQues.findQuestionsByQuestionnaire(questionnaireId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Question supprimée"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-questions");
            selectedQuestion = null;
        }
    }
    
    public void deleteSelectedQuestions() {
        if(selectedQuestions != null && !selectedQuestions.isEmpty()) {
        	 for (Question emp : selectedQuestions) {
                 daoQues.remove(emp);
             }           
             questions=daoQues.findQuestionsByQuestionnaire(questionnaireId);

        	 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Questions supprimées"));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-questions");
            selectedQuestions = null;
        }
    }
    
    public String getDeleteButtonMessage() {
        if(selectedQuestions != null && !selectedQuestions.isEmpty()) {
            int size = selectedQuestions.size();
            return size > 1 ? size + " questions sélectionnées" : "1 question sélectionnée";
        }
        return "Delete";
    }

	public List<Reponse> getReponses() {
		return reponses;
	}

	public void setReponses(List<Reponse> reponses) {
		this.reponses = reponses;
	}

	public List<Reponse> getSelecedreponses() {
		return selecedreponses;
	}

	public void setSelecedreponses(List<Reponse> selecedreponses) {
		this.selecedreponses = selecedreponses;
	}
}
