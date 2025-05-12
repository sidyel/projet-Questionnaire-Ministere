package managedBean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.dao.GenericDAO;
import org.entitee.Question;
import org.entitee.Questionnaire;
import org.entitee.Reponse;
// Importez votre GenericDAO ou service si nécessaire

@Named
@ViewScoped
public class ReponseBean implements Serializable {

    private List<Question> questions=new ArrayList<Question>();
    private Question question;
    private Reponse reponse=new Reponse();
    private int questionnaireId;
    
    GenericDAO<Question> daoQues =new GenericDAO<>(Question.class);
    GenericDAO<Questionnaire> daoQuestinnaire=new GenericDAO<>(Questionnaire.class);
   
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
        } else {
            questions = new ArrayList<>();
        }
    }


    // Getters et setters
    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    public int getQuestionnaireId() {
        return questionnaireId;
    }
    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }
    
    public boolean hasSelectedQuestions() {
        return questions != null && !questions.isEmpty();
    }
    
    // Exemple d'actions CRUD (à compléter selon vos besoins)
    public void openNew() {
       setQuestion(new Question());
    }
    
    


	public Question getQuestion() {
		return question;
	}


	public void setQuestion(Question question) {
		this.question = question;
	}


	public Reponse getReponse() {
		return reponse;
	}


	public void setReponse(Reponse reponse) {
		this.reponse = reponse;
	}
    
}
